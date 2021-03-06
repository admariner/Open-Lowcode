/********************************************************************************
 * Copyright (c) 2020 [Open Lowcode SAS](https://openlowcode.com/)
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0 .
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package org.openlowcode.design.data.properties.basic;

import java.io.IOException;
import java.util.ArrayList;

import org.openlowcode.design.action.DynamicActionDefinition;
import org.openlowcode.design.data.ArgumentContent;
import org.openlowcode.design.data.DataObjectDefinition;
import org.openlowcode.design.data.MethodAdditionalProcessing;
import org.openlowcode.design.data.Property;
import org.openlowcode.design.data.PropertyGenerics;
import org.openlowcode.design.data.argument.ObjectIdArgument;
import org.openlowcode.design.generation.SourceGenerator;
import org.openlowcode.design.generation.StringFormatter;
import org.openlowcode.design.module.Module;
import org.openlowcode.tools.misc.NamedList;

/**
 * Property for object that is left for a link to master
 * 
 * @author <a href="https://openlowcode.com/" rel="nofollow">Open Lowcode
 *         SAS</a>
 *
 * @param <E> current object
 * @param <F> link object
 */
public class RightForLinkToMaster<E extends DataObjectDefinition, F extends DataObjectDefinition>
extends
Property<RightForLink<E, F>> {

private E linkdataobject;
private F leftobjectforlink;
private Versioned versioned;
private NamedList<DynamicActionDefinition> actionsonobjectid;
private NamedList<DynamicActionDefinition> actionsonselectedlinkid;
private NamedList<DynamicActionDefinition> actiononselectedleftobjectid;
private LinkObjectToMaster<F, ?> linkobjecttomaster;
private WidgetDisplayPriority priorityforlinkfromrighttable;

/**
* @return the widget to show links from right object
*/
public Widget getLinkFromRightTableWidget() {
return new LinkToMasterFromRightTable(this);
}

/**
* link object property on the link object object
* 
* @return link object property on the link object
*/
public LinkObjectToMaster<F, ?> getLinkObjectProperty() {
return linkobjecttomaster;
}

/**
* creates a right for link property
* 
* @param linkdataobject                link data object
* @param leftobjectforlink             left object of the link
* @param linkobject                    link object property
* @param priorityforlinkfromrighttable the priority, if specified for the right
*                                      table in the right object standard show
*                                      page
*/
public RightForLinkToMaster(
	E linkdataobject,
	F leftobjectforlink,
	LinkObjectToMaster<F, ?> linkobjecttomaster,
	WidgetDisplayPriority priorityforlinkfromrighttable) {
super(linkdataobject.getName(), "RIGHTFORLINKTOMASTER");
this.linkdataobject = linkdataobject;
this.leftobjectforlink = leftobjectforlink;
this.linkobjecttomaster = linkobjecttomaster;
this.priorityforlinkfromrighttable = priorityforlinkfromrighttable;
}

@Override
public void controlAfterParentDefinition() {

	versioned = (Versioned) parent.getPropertyByName("VERSIONED");
this.addDependentProperty(versioned);
MethodAdditionalProcessing deleteobjectcheck = new MethodAdditionalProcessing(true,
		versioned.getRelatedUniqueIdentified().getDataAccessMethod("DELETE"));
this.addMethodAdditionalProcessing(deleteobjectcheck);
MethodAdditionalProcessing controlinkconstraintbeforeupdate = new MethodAdditionalProcessing(true,
		versioned.getRelatedUniqueIdentified().getDataAccessMethod("UPDATE"));
this.addMethodAdditionalProcessing(controlinkconstraintbeforeupdate);

this.addPropertyGenerics(new PropertyGenerics("LINKOBJECTTOMASTER", linkdataobject, linkobjecttomaster));
this.addPropertyGenerics(new PropertyGenerics("LEFTOBJECTFORLINKTOMASTER", leftobjectforlink,
		leftobjectforlink.getPropertyByName("UNIQUEIDENTIFIED")));

actionsonobjectid = new NamedList<DynamicActionDefinition>();
actionsonselectedlinkid = new NamedList<DynamicActionDefinition>();
actiononselectedleftobjectid = new NamedList<DynamicActionDefinition>();
}

/**
* @return get the left object for the link
*/
public F getLeftObjectForLink() {
return this.leftobjectforlink;
}

@Override
public String getJavaType() {
return "#NOTIMPLEMENTED#";
}

@Override
public void writeDependentClass(SourceGenerator sg, Module module) throws IOException {
}

/**
* @return get the data object holding the link
*/
public DataObjectDefinition getLinkObjectDefinition() {

return this.linkdataobject;
}

@Override
public ArrayList<DataObjectDefinition> getExternalObjectDependence() {
ArrayList<DataObjectDefinition> dependencies = new ArrayList<DataObjectDefinition>();
dependencies.add(linkdataobject);
dependencies.add(leftobjectforlink);
return dependencies;
}

/**
* adds an action using the current (right object for link) data object id
* (probably useless see github issue #16)
* 
* @param action action to add
*/
public void addActionOnObjectId(DynamicActionDefinition action) {
if (action.getInputArguments().getSize() == 1)
	throw new RuntimeException("you can add an action on object id only if it has 1 argument, action "
			+ action.getName() + " has " + action.getInputArguments().getSize() + ".");
ArgumentContent uniqueinputargument = action.getInputArguments().get(1);
if (!(uniqueinputargument instanceof ObjectIdArgument))
	throw new RuntimeException("the first argument of " + action.getName()
			+ " should be ObjectidArgument, it is actually " + uniqueinputargument.getClass().getName() + ".");
ObjectIdArgument objectidargument = (ObjectIdArgument) uniqueinputargument;
DataObjectDefinition objectforid = objectidargument.getObject();
if (objectforid != parent) {
	throw new RuntimeException("objectid should be of consistent type, actionid type = "
			+ objectforid.getOwnermodule().getName() + "/" + objectforid.getName() + ", object parentid type = "
			+ parent.getOwnermodule().getName() + "/" + parent.getName());
}

actionsonobjectid.add(action);
}

/**
* adds an action on the selected link id
* 
* @param action an action that should have as unique input argument a data
*               object id for the link object
*/
public void addActionOnSelectedLinkId(DynamicActionDefinition action) {
if (action.getInputArguments().getSize() == 1)
	throw new RuntimeException("you can add an action on selected link id only if it has 1 argument, action "
			+ action.getName() + " has " + action.getInputArguments().getSize() + ".");
ArgumentContent uniqueinputargument = action.getInputArguments().get(1);
if (!(uniqueinputargument instanceof ObjectIdArgument))
	throw new RuntimeException("the first argument of " + action.getName()
			+ " should be ObjectidArgument, it is actually " + uniqueinputargument.getClass().getName() + ".");
ObjectIdArgument objectidargument = (ObjectIdArgument) uniqueinputargument;
DataObjectDefinition objectforid = objectidargument.getObject();
if (objectforid != linkdataobject) {
	throw new RuntimeException("objectid should be of consistent type, actionid type = "
			+ objectforid.getOwnermodule().getName() + "/" + objectforid.getName() + ", object parentid type = "
			+ linkdataobject.getOwnermodule().getName() + "/" + linkdataobject.getName());
}

actionsonselectedlinkid.add(action);
}

/**
* adds an action on the selected right object id
* 
* @param action an action that should have as unique inut argument a data
*               object id for the right object
*/
public void addActionOnSelectedLeftObjectId(DynamicActionDefinition action) {
if (action.getInputArguments().getSize() == 1)
	throw new RuntimeException(
			"you can add an action on selected link left object id only if it has 1 argument, action "
					+ action.getName() + " has " + action.getInputArguments().getSize() + ".");
ArgumentContent uniqueinputargument = action.getInputArguments().get(1);
if (!(uniqueinputargument instanceof ObjectIdArgument))
	throw new RuntimeException("the first argument of " + action.getName()
			+ " should be ObjectidArgument, it is actually " + uniqueinputargument.getClass().getName() + ".");
ObjectIdArgument objectidargument = (ObjectIdArgument) uniqueinputargument;
DataObjectDefinition objectforid = objectidargument.getObject();
if (objectforid != leftobjectforlink) {
	throw new RuntimeException("objectid should be of consistent type, actionid type = "
			+ objectforid.getOwnermodule().getName() + "/" + objectforid.getName() + ", object parentid type = "
			+ leftobjectforlink.getOwnermodule().getName() + "/" + leftobjectforlink.getName());
}

actiononselectedleftobjectid.add(action);
}

@Override
public String[] getPropertyInitMethod() {
return new String[0];
}

@Override
public String[] getPropertyExtractMethod() {
return new String[0];
}

@Override
public void setFinalSettings() {
// nothing to do
}

@Override
public String[] getPropertyDeepCopyStatement() {

return null;
}

/**
* a widget showing in a table all the links from the right object
* 
* @author <a href="https://openlowcode.com/" rel="nofollow">Open Lowcode
*         SAS</a>
*
*/
public class LinkToMasterFromRightTable
	extends
	Widget {
private RightForLinkToMaster<E, F> rightforlinktomaster;

/**
 * create a link from right table for the given right for link
 * 
 * @param rightforlinktomaster right for link property
 */
LinkToMasterFromRightTable(RightForLinkToMaster<E, F> rightforlinktomaster) {
	super("RIGHTFORLINKTABLE");
	this.rightforlinktomaster = rightforlinktomaster;
}

@Override
public String[] getImportStatements() {
	ArrayList<String> importstatements = new ArrayList<String>();
	importstatements.add("import org.openlowcode.server.action.SInlineActionRef;");
	importstatements.add("import org.openlowcode.server.graphic.widget.SObjectStorage;");
	importstatements.add("import org.openlowcode.server.graphic.widget.SFieldSearcher;");
	importstatements.add("import org.openlowcode.server.graphic.widget.SObjectArrayField;");
	importstatements.add("import org.openlowcode.server.graphic.widget.SObjectSearcher;");
	importstatements.add("import org.openlowcode.server.graphic.widget.SPopupButton;");

	String linkobjectclass = StringFormatter
			.formatForJavaClass(rightforlinktomaster.getLinkObjectDefinition().getName());
	String linkobjectvariable = StringFormatter
			.formatForAttribute(rightforlinktomaster.getLinkObjectDefinition().getName());
	LinkObjectToMaster<F, ?> linkobject = rightforlinktomaster.getLinkObjectProperty();

	Module linkobjectmodule = rightforlinktomaster.getLinkObjectDefinition().getOwnermodule();
	Module module = rightforlinktomaster.getParent().getOwnermodule();
	importstatements.add("import " + linkobjectmodule.getPath() + ".data." + linkobjectclass + "Definition;");

	importstatements.add("import " + linkobjectmodule.getPath() + ".action.generated.AtgCreate"
			+ linkobjectvariable + "andshowright"
			+ StringFormatter.formatForAttribute(linkobject.getRightobjectforlink().getName()) + "Action;");
	importstatements.add("import " + linkobjectmodule.getPath() + ".action.generated.AtgDelete"
			+ linkobjectvariable + "andshowrightAction;");
	if (linkobject.getBusinessRuleNumber() > 0) {
		importstatements.add("import " + module.getPath() + ".action.generated.AtgSearchleft"
				+ linkobject.getLeftobjectforlink().getName().toLowerCase() + "for"
				+ rightforlinktomaster.getLinkObjectDefinition().getName().toLowerCase() + "Action;");
	}

	return importstatements.toArray(new String[0]);
}

@Override
public void generateWidgetCode(SourceGenerator sg, Module module, String locationname,DataObjectDefinition companion) throws IOException {

	DataObjectDefinition linkobject = rightforlinktomaster.getLinkObjectDefinition();
	String objectvariable = StringFormatter.formatForAttribute(rightforlinktomaster.getParent().getName());
	String objectclass = StringFormatter.formatForJavaClass(rightforlinktomaster.getParent().getName());

	String linkobjectclass = StringFormatter.formatForJavaClass(linkobject.getName());
	String linkobjectvariable = StringFormatter.formatForAttribute(linkobject.getName());
	String leftobjectvariable = StringFormatter
			.formatForAttribute(rightforlinktomaster.getLeftObjectForLink().getName());
	String leftobjectclass = StringFormatter
			.formatForJavaClass(rightforlinktomaster.getLinkObjectProperty().getLeftobjectforlink().getName());

	sg.wl("");
	sg.wl("		// ------------------------------------------------------------------------------------------");
	sg.wl("		// Display " + linkobjectclass);
	sg.wl("		// ------------------------------------------------------------------------------------------");
	sg.wl("");
	sg.wl("		" + locationname + ".addElement(new SPageText(\""
			+ rightforlinktomaster.getLinkObjectProperty().getLabelFromRight() + "\",SPageText.TYPE_TITLE,this));");
	sg.wl("		SObjectArray<" + linkobjectclass + "> right" + linkobjectvariable + "s = new SObjectArray<"
			+ linkobjectclass + ">(\"RIGHT" + linkobjectclass.toUpperCase() + "\",");
	sg.wl("				this.getRightforlinktomasterfor" + linkobjectvariable + "(),");
	sg.wl("				" + linkobjectclass + ".getDefinition(),");
	sg.wl("				this);");
	sg.wl("		right" + linkobjectvariable + "s.addDisplayProfile(" + linkobjectclass + "Definition.get"
			+ linkobjectclass + "Definition().getDisplayProfileHiderightobjectfields());");


	sg.wl("		");
	sg.wl("		AtgShow" + leftobjectvariable + "Action.ActionRef showleft" + leftobjectvariable + "for"
			+ linkobjectvariable + "action = AtgShow" + leftobjectvariable + "Action.get().getActionRef();");
	sg.wl("		showleft" + leftobjectvariable + "for" + linkobjectvariable + "action.setId(right"
			+ linkobjectvariable + "s.getAttributeInput(" + linkobjectclass + ".getLfidMarker()));");
	sg.wl("		right" + linkobjectvariable + "s.addDefaultAction(showleft" + leftobjectvariable + "for"
			+ linkobjectvariable + "action);");
	sg.wl("		");

	sg.wl("		SComponentBand addtoright" + linkobjectvariable
			+ "s = new SComponentBand(SComponentBand.DIRECTION_DOWN,this);");
	sg.wl("		");
	sg.wl("		// add link");
	sg.wl("		SPageText titleforright" + linkobjectvariable + " = new  SPageText(\"Add a new "
			+ rightforlinktomaster.getLinkObjectProperty().getLabelFromRight() + "\",SPageText.TYPE_TITLE,this);");
	sg.wl("		addtoright" + linkobjectvariable + "s.addElement(titleforright" + linkobjectvariable + ");");
	sg.wl("				");

	LinkObjectToMaster<F, ?> linkobjectproperty = rightforlinktomaster.getLinkObjectProperty();
	if (linkobjectproperty.getBusinessRuleNumber() > 0) {
		// if business rule on link, use specific object searcher for right object
		sg.wl("		SObjectSearcher<" + leftobjectclass + "> " + leftobjectvariable + "searchforaddtoright"
				+ linkobjectvariable + "s = AtgSearch" + leftobjectvariable + "Page.getsearchpanel(");
		sg.wl("			((AtgSearchleft" + leftobjectvariable + "for" + linkobjectvariable
				+ "Action)AtgSearchleft" + leftobjectvariable + "for" + linkobjectvariable
				+ "Action.get()).new Specific" + leftobjectclass + "Searcher(");
		sg.wl("					objectdisplaydefinition.getAttributeInput(" + objectclass
				+ ".getIdMarker())),");
		sg.wl("			this,\"" + linkobject.getName().toUpperCase() + "SEARCHFROMRIGHT\");");
		sg.wl("		" + leftobjectvariable + "searchforaddtoright" + linkobjectvariable
				+ "s .getresultarray().setAllowMultiSelect(); ");
		sg.wl("		addtoright" + linkobjectvariable + "s.addElement(" + leftobjectvariable
				+ "searchforaddtoright" + linkobjectvariable + "s);");
		sg.wl("");
	} else {
		// if no business rule on link, use standard object searcher for right object
		sg.wl("		SObjectSearcher<" + leftobjectclass + "> " + leftobjectvariable + "searchforaddtoright"
				+ linkobjectvariable + "s = AtgSearch" + leftobjectvariable + "Page.getsearchpanel(this,\""
				+ linkobject.getName().toUpperCase() + "SEARCHFROMRIGHT\");");
		sg.wl("		" + leftobjectvariable + "searchforaddtoright" + linkobjectvariable
				+ "s .getresultarray().setAllowMultiSelect(); ");
		sg.wl("		addtoright" + linkobjectvariable + "s.addElement(" + leftobjectvariable
				+ "searchforaddtoright" + linkobjectvariable + "s);");
		sg.wl("");
	}

	sg.wl("		SObjectDisplay<" + linkobjectclass + "> blanklinkforaddtoright" + linkobjectvariable
			+ "s = new  SObjectDisplay<" + linkobjectclass + ">(\"BLANK" + linkobject.getName().toUpperCase()
			+ "FORADDFROMRIGHT\",");
	sg.wl("			this.getRightforlinktomasterfor" + linkobjectvariable + "blankforadd(), " + linkobjectclass
			+ ".getDefinition(),this, false);");
	sg.wl("		blanklinkforaddtoright" + linkobjectvariable + "s.setHideReadOnly();");
	sg.wl("				");
	sg.wl("		addtoright" + linkobjectvariable + "s.addElement(blanklinkforaddtoright" + linkobjectvariable
			+ "s);");
	sg.wl("				");
	sg.wl("		AtgCreate" + linkobjectvariable + "andshowright" + objectvariable
			+ "Action.ActionRef createlinkactionforaddtoright" + linkobjectvariable + "s = AtgCreate"
			+ linkobjectvariable + "andshowright" + objectvariable + "Action.get().getActionRef();");
	sg.wl("				");
	sg.wl("		createlinkactionforaddtoright" + linkobjectvariable + "s.setRight" + objectvariable
			+ "id(objectdisplaydefinition.getAttributeInput(" + objectclass + ".getIdMarker())); ");
	sg.wl("		createlinkactionforaddtoright" + linkobjectvariable + "s.set" + linkobjectclass
			+ "(blanklinkforaddtoright" + linkobjectvariable + "s.getObjectInput());");
	sg.wl("		createlinkactionforaddtoright" + linkobjectvariable + "s.setLeft" + leftobjectvariable + "id("
			+ leftobjectvariable + "searchforaddtoright" + linkobjectvariable
			+ "s.getresultarray().getAttributeArrayInput(" + leftobjectclass + ".getIdMarker())); ");
	sg.wl("					");
	sg.wl("		SActionButton createlinkbuttonforaddtoright" + linkobjectvariable
			+ " = new SActionButton(\"Add Link\", createlinkactionforaddtoright" + linkobjectvariable
			+ "s, this);");
	sg.wl("		addtoright" + linkobjectvariable + "s.addElement(createlinkbuttonforaddtoright"
			+ linkobjectvariable + ");");
	sg.wl("		");
	sg.wl("		SPopupButton addtoright" + linkobjectvariable + "sbutton = new SPopupButton(this,addtoright"
			+ linkobjectvariable + "s,\"Add\",\"you can link an existing " + leftobjectclass + " to this "
			+ rightforlinktomaster.getParent().getLabel() + " through the "
			+ rightforlinktomaster.getLinkObjectProperty().getLabelFromRight() + "\",createlinkactionforaddtoright"
			+ linkobjectvariable + "s);");
	sg.wl("		SComponentBand right" + linkobjectvariable
			+ "buttonbar = new SComponentBand(SComponentBand.DIRECTION_RIGHT, this);");
	sg.wl("		right" + linkobjectvariable + "buttonbar.addElement(addtoright" + linkobjectvariable
			+ "sbutton);");
	sg.wl("		");
	sg.wl("		AtgDelete" + linkobjectvariable + "andshowrightAction.ActionRef deleteoneofright"
			+ linkobjectvariable + " = AtgDelete" + linkobjectvariable
			+ "andshowrightAction.get().getActionRef();");
	sg.wl("		deleteoneofright" + linkobjectvariable + ".set" + linkobjectclass + "id(right"
			+ linkobjectvariable + "s.getAttributeInput(" + linkobjectclass + ".getIdMarker())); ");
	sg.wl("		SActionButton deleteoneofright" + linkobjectvariable
			+ "button = new SActionButton(\"Delete selected\", deleteoneofright" + linkobjectvariable
			+ ", this);");
	sg.wl("		right" + linkobjectvariable + "buttonbar.addElement(deleteoneofright" + linkobjectvariable
			+ "button);");
	sg.wl("		" + locationname + ".addElement(right" + linkobjectvariable + "buttonbar);");
	sg.wl("		" + locationname + ".addElement(right" + linkobjectvariable + "s);");
}

@Override
public WidgetDisplayPriority getWidgetPriority() {
	return rightforlinktomaster.priorityforlinkfromrighttable;
}

}

}
