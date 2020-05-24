/********************************************************************************
 * Copyright (c) 2019-2020 [Open Lowcode SAS](https://openlowcode.com/)
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0 .
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package org.openlowcode.design.advanced;

import java.io.IOException;

import org.openlowcode.design.data.ChoiceCategory;
import org.openlowcode.design.data.ChoiceField;
import org.openlowcode.design.generation.SourceGenerator;
import org.openlowcode.design.generation.StringFormatter;

/**
 * A column criteria based on a choice field
 * 
 * @author <a href="https://openlowcode.com/" rel="nofollow">Open Lowcode
 *         SAS</a>
 *
 */
public class ChoiceColumnCriteria
		extends
		ColumnCriteria {

	private ChoiceField fieldforcolumncriteria;

	/**
	 * create a choice column criteria based on a choice field on the object having
	 * the the main value field
	 * 
	 * @param node                   node used for the main report value
	 * @param fieldforcolumncriteria choice field used to sort value by columns
	 */
	public ChoiceColumnCriteria(SmartReportNode node, ChoiceField fieldforcolumncriteria) {
		this(node, fieldforcolumncriteria, null);

	}

	/**
	 * create a choice column criteria based on a choice field on the object having
	 * the the main value field
	 * 
	 * @param node                   node used for the main report value
	 * @param fieldforcolumncriteria choice field used to sort value by columns
	 * @param suffix                 sufix of the main value
	 */
	public ChoiceColumnCriteria(SmartReportNode node, ChoiceField fieldforcolumncriteria, String suffix) {
		super(node, suffix);
		if (fieldforcolumncriteria == null)
			throw new RuntimeException("ChoiceField cannot be null");
		if (fieldforcolumncriteria.getParentObject() != node.getRelevantObject())
			throw new RuntimeException(
					"object for node " + node.getRelevantObject().getName() + " and choice field parent "
							+ fieldforcolumncriteria.getParentObject().getName() + " are not consistent");
		this.fieldforcolumncriteria = fieldforcolumncriteria;

	}

	/**
	 * create a choice column criteria based on a choice field on the object having
	 * the the main value field
	 * 
	 * @param node                   node used for the main report value
	 * @param fieldforcolumncriteria choice field used to sort value by columns
	 * @param suffix                 suffix of the main value
	 * @param columnindex            index for ordering columns. Columns with same
	 *                               index are ordered together
	 */
	public ChoiceColumnCriteria(
			SmartReportNode node,
			ChoiceField fieldforcolumncriteria,
			String suffix,
			int columnindex) {
		super(node, suffix, columnindex);
		if (fieldforcolumncriteria == null)
			throw new RuntimeException("ChoiceField cannot be null");
		if (fieldforcolumncriteria.getParentObject() != node.getRelevantObject())
			throw new RuntimeException(
					"object for node " + node.getRelevantObject().getName() + " and choice field parent "
							+ fieldforcolumncriteria.getParentObject().getName() + " are not consistent");
		this.fieldforcolumncriteria = fieldforcolumncriteria;

	}

	/**
	 * @return get the choice field used as column criteria
	 */
	public ChoiceField getFieldForColumnCriteria() {
		return this.fieldforcolumncriteria;
	}

	@Override
	public String generateLabelExtractor() {
		String fieldclassname = StringFormatter.formatForJavaClass(this.getFieldForColumnCriteria().getName());
		return "(a)->((a.get" + fieldclassname + "()!=null?a.get" + fieldclassname
				+ "().getDisplayValue():\"Unspecified\"))";
	}

	@Override
	public String generatePayloadExtractor() {
		String fieldclassname = StringFormatter.formatForJavaClass(this.getFieldForColumnCriteria().getName());
		return "(a)->(a.get" + fieldclassname + "())";
	}

	@Override
	protected void writeColumnValueGenerator(SourceGenerator sg, ObjectReportNode objectReportNode, String prefix)
			throws IOException {
		String suffixdef = "";
		if (this.getSuffix() != null)
			suffixdef = "+\"" + this.getSuffix() + "\"";
		sg.wl("			String columnvalue = (this"
				+ StringFormatter.formatForAttribute(objectReportNode.getRelevantObject().getName()) + "step" + prefix
				+ ".get" + StringFormatter.formatForJavaClass(fieldforcolumncriteria.getName()) + "()!=null?this"
				+ StringFormatter.formatForAttribute(objectReportNode.getRelevantObject().getName()) + "step" + prefix
				+ ".get" + StringFormatter.formatForJavaClass(fieldforcolumncriteria.getName())
				+ "().getDisplayValue():\"Unspecified\")" + suffixdef + ";");

	}

	@Override
	public String getColumnPayloadClass() {
		return "ChoiceValue<" + StringFormatter.formatForJavaClass(this.fieldforcolumncriteria.getChoice().getName())
				+ "ChoiceDefinition>";
	}

	@Override
	protected String[] getImportStatements() {
		ChoiceCategory choice = this.fieldforcolumncriteria.getChoice();
		return new String[] { "import " + choice.getParentModule().getPath() + ".data.choice."
				+ StringFormatter.formatForJavaClass(choice.getName()) + "ChoiceDefinition;" };
	}

}
