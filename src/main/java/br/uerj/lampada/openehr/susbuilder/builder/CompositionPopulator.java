/*
 * component:   "openEHR Java Reference Implementation"
 * description: "Class SkeletonGenerator"
 * keywords:    "rm-skeleton"
 *
 * author:      "Rong Chen <rong.acode@gmail.com>"
 * copyright:   "Copyright (c) 2009,2010 Cambio Healthcare Systems, Sweden"
 * license:     "See notice at bottom of class"
 *
 * file:        "$URL$"
 * revision:    "$LastChangedRevision$"
 * last_change: "$LastChangedDate$"
 */

package br.uerj.lampada.openehr.susbuilder.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openehr.am.archetype.Archetype;
import org.openehr.am.archetype.assertion.Assertion;
import org.openehr.am.archetype.assertion.ExpressionBinaryOperator;
import org.openehr.am.archetype.assertion.ExpressionItem;
import org.openehr.am.archetype.assertion.ExpressionLeaf;
import org.openehr.am.archetype.constraintmodel.ArchetypeInternalRef;
import org.openehr.am.archetype.constraintmodel.ArchetypeSlot;
import org.openehr.am.archetype.constraintmodel.CAttribute;
import org.openehr.am.archetype.constraintmodel.CComplexObject;
import org.openehr.am.archetype.constraintmodel.CDomainType;
import org.openehr.am.archetype.constraintmodel.CMultipleAttribute;
import org.openehr.am.archetype.constraintmodel.CObject;
import org.openehr.am.archetype.constraintmodel.CPrimitiveObject;
import org.openehr.am.archetype.constraintmodel.CSingleAttribute;
import org.openehr.am.archetype.constraintmodel.primitive.CBoolean;
import org.openehr.am.archetype.constraintmodel.primitive.CDate;
import org.openehr.am.archetype.constraintmodel.primitive.CDateTime;
import org.openehr.am.archetype.constraintmodel.primitive.CDuration;
import org.openehr.am.archetype.constraintmodel.primitive.CInteger;
import org.openehr.am.archetype.constraintmodel.primitive.CPrimitive;
import org.openehr.am.archetype.constraintmodel.primitive.CReal;
import org.openehr.am.archetype.constraintmodel.primitive.CString;
import org.openehr.am.archetype.constraintmodel.primitive.CTime;
import org.openehr.am.archetype.ontology.ArchetypeTerm;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvOrdinal;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvQuantity;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvQuantityItem;
import org.openehr.am.openehrprofile.datatypes.quantity.Ordinal;
import org.openehr.am.openehrprofile.datatypes.text.CCodePhrase;
import org.openehr.am.template.TermMap;
import org.openehr.build.RMObjectBuilder;
import org.openehr.build.RMObjectBuildingException;
import org.openehr.build.SystemValue;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.generic.PartyIdentified;
import org.openehr.rm.common.generic.PartySelf;
import org.openehr.rm.datatypes.basic.DvBoolean;
import org.openehr.rm.datatypes.encapsulated.DvMultimedia;
import org.openehr.rm.datatypes.encapsulated.DvParsable;
import org.openehr.rm.datatypes.quantity.DvOrdinal;
import org.openehr.rm.datatypes.quantity.DvQuantity;
import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.quantity.datetime.DvDuration;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.datatypes.uri.DvURI;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.PartyRef;
import org.openehr.rm.support.identification.TemplateID;
import org.openehr.rm.support.identification.TerminologyID;
import org.openehr.rm.support.identification.UIDBasedID;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.measurement.SimpleMeasurementService;
import org.openehr.rm.support.terminology.TerminologyAccess;
import org.openehr.rm.support.terminology.TerminologyService;
import org.openehr.terminology.SimpleTerminologyService;

import br.uerj.lampada.openehr.susbuilder.mapping.PathMapping;
import br.uerj.lampada.openehr.susbuilder.mapping.PathState;

public class CompositionPopulator {

	private static final String DEFAULT_CODED_TEXT = "coded text value";
	private static final String DEFAULT_COUNT = "1";
	// default values
	private static final String DEFAULT_DATE = "2010-01-01";
	private static final String DEFAULT_DATE_TIME = "2010-01-01T10:00:00";
	private static final String DEFAULT_DURATION = "PT1H";
	private static final String DEFAULT_TIME = "10:00:00";
	private static final String DEFINING_CODE = "defining_code";

	private static final Logger log = Logger
			.getLogger(CompositionPopulator.class);

	private static final String MAGNITUDE = "magnitude";

	private static final String NULL_FLAVOUR = "null_flavour";

	private static final DvCodedText NULL_FLAVOUR_VALUE_NM = new DvCodedText(
			"no information", new CodePhrase(TerminologyService.OPENEHR, "271"));

	private static final DvCodedText NULL_FLAVOUR_VALUE_UN = new DvCodedText(
			"unknown", new CodePhrase(TerminologyService.OPENEHR, "253"));

	private static final String ORIGIN = "origin";

	private static final String SETTING = "setting";

	private static final String START_TIME = "start_time";

	private static final String TIME = "time";

	private static final String TIMING = "timing";

	// rm attribute names
	private static final String VALUE = "value";

	private RMObjectBuilder builder;

	private DvCodedText category;

	private CodePhrase charset;// = new CodePhrase("IANA_character-sets",
								// "UTF-8");

	private Map<String, Object> compositionData;

	private CodePhrase country;

	private CodePhrase language;// = new CodePhrase("ISO_639-1", "sv");

	private MeasurementService measurementService;

	private TerminologyAccess openEHRTerminology;

	private PartyIdentified party;

	private String patientId;

	private String rmVersion;

	private DvCodedText setting;

	private TemplateID templateId;

	private TerminologyService terminologyService;

	private TermMap termMap;

	private UIDBasedID uid;

	public CompositionPopulator(CodePhrase language, CodePhrase charset,
			TemplateID templateId, String rmVersion, DvCodedText setting,
			CodePhrase country, DvCodedText category, PartyIdentified party)
			throws Exception {
		this.language = language;
		this.charset = charset;
		this.templateId = templateId;
		this.rmVersion = rmVersion;
		this.setting = setting;

		this.country = country;
		this.category = category;
		this.party = party;

		this.compositionData = new HashMap<String, Object>();

		this.measurementService = SimpleMeasurementService.getInstance();
		this.terminologyService = SimpleTerminologyService.getInstance();
		this.openEHRTerminology = terminologyService
				.terminology(TerminologyService.OPENEHR);

		this.builder = initializeBuilder();
		this.termMap = new TermMap();
	}

	/**
	 * Create RM object tree with specified template
	 * 
	 * @param patientId
	 * 
	 * @param archetype
	 * @param templateId
	 * @param archetypeMap
	 * @param compositionData
	 *            data that will fill in the composition slots
	 * @param strategy
	 * @return
	 * @throws Exception
	 */
	public Object create(String patientId, String uid, Archetype archetype,
			Map<String, Archetype> archetypeMap,
			Map<String, Object> compositionData,
			Map<String, PathState> pathState) throws Exception {
		this.uid = new HierObjectID(uid);
		this.patientId = patientId;
		this.compositionData = compositionData;
		return createComplexObject(archetype.getDefinition(), archetype,
				archetypeMap, pathState, null, null);
	}

	private void addEntryValues(Map<String, Object> valueMap,
			Archetype archetype) throws Exception {

		Archetyped archetypeDetails = new Archetyped(
				archetype.getArchetypeId(), templateId, rmVersion);

		valueMap.put("subject", subject());
		// valueMap.put("provider", provider());
		valueMap.put("encoding", charset);
		valueMap.put("language", language);
		valueMap.put("archetype_details", archetypeDetails);
	}

	private Object createArchetypeInternalRefObject(ArchetypeInternalRef robj,
			Archetype archetype, Map<String, Archetype> archetypeMap,
			Map<String, PathState> pathState, String slotPath) throws Exception {

		log.debug("create archetype internal reference object "
				+ robj.getRmTypeName());

		String pathNodeId = robj.path().replaceAll("\\w+$", "");
		String logicalNodeId = pathNodeId.replaceAll("^.*"
				+ archetype.getArchetypeId().getValue() + ".*?/", "/");
		String targetNodeId = robj.getTargetPath().replaceAll("^/.*/", "");
		String path = logicalNodeId + "" + targetNodeId;
		Object obj = archetype.getPathNodeMap().get(path);

		return createComplexObject((CComplexObject) obj, archetype,
				archetypeMap, pathState, robj.path() + "["
						+ ((CComplexObject) obj).getNodeId() + "]", slotPath);
	}

	private Object createArchetypeSlotObject(ArchetypeSlot sobj,
			Archetype archetype, Map<String, Archetype> archetypeMap,
			Map<String, PathState> pathState, String pathRef) throws Exception {
		log.debug("create archetype internal reference object "
				+ sobj.getRmTypeName());

		if (pathRef != null) {
			String inPattern = null;
			String exPattern = null;

			if (sobj.getIncludes() != null) {
				Assertion inAssertion = sobj.getIncludes().iterator().next();
				ExpressionItem inRightOp = ((ExpressionBinaryOperator) inAssertion
						.getExpression()).getRightOperand();
				inPattern = ((CString) ((ExpressionLeaf) inRightOp).getItem())
						.getPattern();
			}
			if (sobj.getExcludes() != null) {
				Assertion exAssertion = sobj.getExcludes().iterator().next();
				ExpressionItem exRightOp = ((ExpressionBinaryOperator) exAssertion
						.getExpression()).getRightOperand();
				exPattern = ((CString) ((ExpressionLeaf) exRightOp).getItem())
						.getPattern();
			}

			if (inPattern.startsWith("openEHR")
					&& (exPattern == null || !inPattern.matches(exPattern))) {
				for (String march : archetypeMap.keySet()) {
					if (march.matches(inPattern)) {
						Archetype slot = archetypeMap.get(march);
						return createComplexObject(slot.getDefinition(), slot,
								archetypeMap, pathState, pathRef, "/items["
										+ march + "]");
					}
				}
			}
		}
		return null;
	}

	private Object createAttribute(CAttribute cattribute, Archetype archetype,
			Map<String, Archetype> archetypeMap,
			Map<String, PathState> pathState, String refPath, String slotPath)
			throws Exception {

		log.debug("create attribute " + cattribute.getRmAttributeName());

		List<CObject> children = cattribute.getChildren();
		if (cattribute instanceof CSingleAttribute) {

			log.debug("single attribute..");

			if (children != null && children.size() > 0) {
				CObject cobj = children.get(0);
				return createObject(cobj, archetype, archetypeMap, pathState,
						refPath, slotPath);
			} else {
				return null;
				// throw new Exception("no child for object: "+ refPath
				// +"::"+cattribute.path());
			}
		} else { // multiple c_attribute

			log.debug("multiple attribute..");

			CMultipleAttribute cma = (CMultipleAttribute) cattribute;
			Collection<Object> container = null;
			if (cma.getCardinality().isList()) {
				container = new ArrayList<Object>();
			} else {
				// default container type
				container = new ArrayList<Object>();
			}
			for (CObject cobj : children) {

				log.debug("looping children, required: " + cobj.isRequired());

				if (cobj.isAllowed()) {
					log.debug("required child");
					Object obj = createObject(cobj, archetype, archetypeMap,
							pathState, refPath, slotPath);
					if (obj != null && obj instanceof List) {
						for (Object iobj : (List<Object>) obj) {
							container.add(iobj);
						}
					} else if (obj != null) {
						container.add(obj);
					}
				}

				// special fix for mistaken optional event:
				// events cardinality matches {1..*; unordered} matches {
				// EVENT[at0003] occurrences matches {0..*} matches {
				else if ("events".equals(cma.getRmAttributeName())
						&& "EVENT".equals(cobj.getRmTypeName())) {

					log.debug("mandatory events attribute fix");

					container.add(createObject(cobj, archetype, archetypeMap,
							pathState, refPath, slotPath));
				}
			}

			// // TODO special rule to include first child
			// if (container.isEmpty()) {
			//
			// log.debug("add first child for empty container attribute");
			//
			// // disabled
			// container.add(createObject(children.get(0), archetype, refPath,
			// slotPath));
			// // return null;
			// }

			return container;
		}
	}

	private CodePhrase createCodePhrase(CCodePhrase ccp) throws Exception {
		if (ccp.getDefaultValue() != null) {
			return ccp.getDefaultValue();
		}
		TerminologyID tid = ccp.getTerminologyId();
		List<String> codeList = ccp.getCodeList();

		String code;
		if (codeList == null || codeList.isEmpty()) {
			code = "0123456789";
		} else {
			code = codeList.get(0);
		}
		return new CodePhrase(tid, code);
	}

	/*
	 * Entering point for complex object creation
	 */
	private Object createComplexObject(CComplexObject ccobj,
			Archetype archetype, Map<String, Archetype> archetypeMap,
			Map<String, PathState> pathState, String archetypeReference,
			String archetypeSlot) throws Exception {

		log.debug("create complex object " + ccobj.getRmTypeName());

		String path = PathMapping.getPath(ccobj.path(), archetypeReference,
				archetypeSlot);
		if (!ccobj.isRequired() && !pathState.get(path).isMapped()) {
			return null;
		}

		Map<String, Object> valueMap = new HashMap<String, Object>();

		String nodeId = ccobj.getNodeId();
		if (nodeId != null) {
			DvText name;

			if (nodeId.startsWith("openEHR")) {
				archetype = archetypeMap.get(nodeId);
				if (archetype == null) {
					throw new Exception("unknown archetype for nodeId: "
							+ nodeId);
				}
				name = retrieveName(archetype.getConcept(), archetype);
			} else {
				name = retrieveName(nodeId, archetype);
			}
			valueMap.put("name", name);

			// use archetypeId instead of nodeId for root
			if (nodeId.equals(archetype.getConcept())) {
				nodeId = archetype.getArchetypeId().toString();
			}
			valueMap.put("archetype_node_id", nodeId);
		}

		String rmTypeName = ccobj.getRmTypeName();

		if (ccobj.getAttributes() != null && ccobj.getAttributes().size() > 0) {
			for (CAttribute cattr : ccobj.getAttributes()) {

				path = cattr.path();
				path = PathMapping.getPath(path, archetypeReference,
						archetypeSlot);
				if (cattr.isAllowed() && !cattr.isHiddenOnForm()) {
					Object attrValue = null;
					if (compositionData != null
							&& compositionData.containsKey(path)) {
						Object dv = compositionData.get(path);
						if (dv instanceof List && dv != null) {
							return createListObject(cattr.getRmAttributeName(),
									ccobj, valueMap, (List<Object>) dv);
						} else {
							attrValue = dv;
						}
					} else if (cattr.isRequired()
							|| pathState.get(path).isMapped()) {
						attrValue = createAttribute(cattr, archetype,
								archetypeMap, pathState, archetypeReference,
								archetypeSlot);
					}
					if (attrValue != null) {
						valueMap.put(cattr.getRmAttributeName(), attrValue);
					}

				}
			}
		}

		path = PathMapping.getPath(ccobj.path(), archetypeReference,
				archetypeSlot);

		if (rmTypeName.equals("DV_CODED_TEXT")) {
			return instantiateDvCodedText(valueMap, archetype);
		} else if (rmTypeName.matches("DV_.*")) {
			return instantiateDataValue(rmTypeName, valueMap);
		} else if (rmTypeName.equals("ELEMENT")) {
			return instantiateElement(valueMap, ccobj, pathState.get(path)
					.isMapped(), pathState.get(path).isNull());
		} else {
			return instantiateOtherRMObject(rmTypeName, valueMap, archetype);
		}
	}

	private Object createDomainTypeObject(CDomainType<?> cpo,
			Archetype archetype) throws Exception {

		if (cpo instanceof CDvQuantity) {

			return createDvQuantity((CDvQuantity) cpo);

		} else if (cpo instanceof CCodePhrase) {

			return createCodePhrase((CCodePhrase) cpo);

		} else if (cpo instanceof CDvOrdinal) {

			return createDvOrdinal((CDvOrdinal) cpo, archetype);

		} else {
			throw new Exception("unsupported c_domain_type: " + cpo.getClass());
		}
	}

	private DvOrdinal createDvOrdinal(CDvOrdinal cdo, Archetype archetype)
			throws Exception {

		if (cdo.getDefaultValue() != null) {
			Ordinal o = cdo.getDefaultValue();
			return new DvOrdinal(o.getValue(), new DvCodedText(
					DEFAULT_CODED_TEXT, o.getSymbol()));
		}
		Set<Ordinal> list = cdo.getList();
		if (list == null || list.size() == 0) {
			throw new Exception("empty list of ordinal");
		}
		Ordinal ordinal = list.iterator().next();
		String text = DEFAULT_CODED_TEXT;
		CodePhrase symbol = ordinal.getSymbol();
		String code = symbol.getCodeString();

		if (isLocallyDefined(symbol)) {
			text = retrieveArchetypeTermText(code, archetype);
		} else {
			text = termMap.getText(symbol, cdo.path());
		}

		return new DvOrdinal(ordinal.getValue(), new DvCodedText(text,
				ordinal.getSymbol()));
	}

	private DvQuantity createDvQuantity(CDvQuantity cdq) throws Exception {
		if (cdq.getList() == null || cdq.getList().isEmpty()) {
			return new DvQuantity(0.0);
		}
		CDvQuantityItem item = cdq.getList().get(0);

		double magnitude;
		if (item.getMagnitude() != null) {
			magnitude = item.getMagnitude().getLower();
		} else {
			magnitude = 0;
		}

		return new DvQuantity(item.getUnits(), magnitude, measurementService);
	}

	private Object createListObject(String rmAttributeName,
			CComplexObject ccobj, Map<String, Object> valueMap, List<Object> dv)
			throws RMObjectBuildingException {
		List<Object> container = new ArrayList<Object>();
		String text = ((DvText) (valueMap.get("name"))).getValue();
		for (int i = 0; i < dv.size(); i++) {
			DvText dt = new DvText(text + " " + (i + 1));
			valueMap.put("name", dt);
			valueMap.put(rmAttributeName, dv.get(i));
			container.add(instantiateElement(valueMap, ccobj, true,
					dv.get(i) == null));
		}
		return container;
	}

	private Object createObject(CObject cobj, Archetype archetype,
			Map<String, Archetype> archetypeMap,
			Map<String, PathState> pathState, String refPath, String slotPath)
			throws Exception {

		log.debug("create object with constraint " + cobj.getClass());

		if (cobj instanceof CComplexObject) {

			// no need for templateId at this level
			return createComplexObject((CComplexObject) cobj, archetype,
					archetypeMap, pathState, refPath, slotPath);

		} else if (cobj instanceof CPrimitiveObject) {

			return createPrimitiveTypeObject((CPrimitiveObject) cobj, archetype);

		} else if (cobj instanceof CDomainType) {

			return createDomainTypeObject((CDomainType<?>) cobj, archetype);

		} else if (cobj instanceof ArchetypeInternalRef) {

			return createArchetypeInternalRefObject(
					(ArchetypeInternalRef) cobj, archetype, archetypeMap,
					pathState, slotPath);

		} else if (cobj instanceof ArchetypeSlot) {

			return createArchetypeSlotObject((ArchetypeSlot) cobj, archetype,
					archetypeMap, pathState, refPath);

		} else {
			// TODO skip archetype_slot etc, log.warn?
			return null;
		}
	}

	private Object createPrimitiveTypeObject(CPrimitiveObject cpo,
			Archetype archetype) throws Exception {

		CPrimitive cp = cpo.getItem();

		if (cp.hasDefaultValue()) {
			return cp.defaultValue();
		}

		if (cp instanceof CBoolean) {
			if (((CBoolean) cp).isTrueValid()) {
				return "true";
			} else {
				return "false";
			}

		} else if (cp instanceof CString) {

			return createString((CString) cp);

		} else if (cp instanceof CDate) {

			return DEFAULT_DATE;

		} else if (cp instanceof CTime) {

			return DEFAULT_TIME;

		} else if (cp instanceof CDateTime) {

			return DEFAULT_DATE_TIME;

		} else if (cp instanceof CInteger) {

			return new Integer(0);

		} else if (cp instanceof CReal) {

			return new Double(0);

		} else if (cp instanceof CDuration) {

			CDuration cd = (CDuration) cp;
			DvDuration duration = null;

			if (cd.hasAssignedValue()) {
				duration = cd.assignedValue();

			} else if (cd.hasDefaultValue()) {
				duration = cd.defaultValue();

			} else if (cd.hasAssumedValue()) {
				duration = cd.assumedValue();

			} else if (cd.getInterval() != null) {

				if (cd.getInterval().getLower() != null) {
					duration = cd.getInterval().getLower();

				} else if (cd.getInterval().getUpper() != null) {
					duration = cd.getInterval().getUpper();
				}
			}
			if (duration == null) {
				return DEFAULT_DURATION;
			} else {
				return duration.toString();
			}

		}

		throw new Exception("unsupported primitive type: " + cp.getType());
	}

	private String createString(CString cs) {
		if (cs.defaultValue() != null) {

			return cs.defaultValue();

		} else if (cs.getList() != null && cs.getList().size() > 0) {

			return cs.getList().get(0);

		} else {
			return "string value";
		}
	}

	private RMObjectBuilder initializeBuilder() {
		Map<SystemValue, Object> values = new HashMap<SystemValue, Object>();
		values.put(SystemValue.LANGUAGE, language);
		values.put(SystemValue.ENCODING, charset);

		values.put(SystemValue.TERMINOLOGY_SERVICE, terminologyService);
		values.put(SystemValue.MEASUREMENT_SERVICE, measurementService);
		return RMObjectBuilder.getInstance(values);
	}

	private Object instantiateDataValue(String rmTypeName,
			Map<String, Object> valueMap) throws Exception {
		// deal with missing required attributes in RM
		if ("DV_TEXT".equals(rmTypeName)) {

			if (!valueMap.containsKey(VALUE)) {
				return null;
				// valueMap.put(VALUE, "text value");
			}

		} else if ("DV_BOOLEAN".equals(rmTypeName)) {

			if (valueMap.get(VALUE) != null
					&& valueMap.get(VALUE) instanceof DvBoolean) {
				DvBoolean db = (DvBoolean) valueMap.get(VALUE);
				valueMap.put(VALUE, db.toString());
			}

		} else if ("DV_URI".equals(rmTypeName)
				|| "DV_EHR_URI".equals(rmTypeName)) {

			if (!valueMap.containsKey(VALUE)) {
				return null;
				// valueMap.put(VALUE, DEFAULT_URI);
			}

		} else if ("DV_DATE_TIME".equals(rmTypeName)) {

			if (!valueMap.containsKey(VALUE)) {
				return null;
				// valueMap.put(VALUE, DEFAULT_DATE_TIME);
			}

		} else if ("DV_DATE".equals(rmTypeName)) {

			if (valueMap.get(VALUE) != null
					&& valueMap.get(VALUE) instanceof DvDate) {
				DvDate dd = (DvDate) valueMap.get(VALUE);
				valueMap.put(VALUE, dd.toString());
			}
			//
			if (!valueMap.containsKey(VALUE)) {
				return null;
				// valueMap.put(VALUE, DEFAULT_DATE);
			}

		} else if ("DV_TIME".equals(rmTypeName)) {

			if (!valueMap.containsKey(VALUE)) {
				valueMap.put(VALUE, DEFAULT_TIME);
			}

		} else if ("DV_MULTIMEDIA".equals(rmTypeName)) {

			String alternateText = "alternative text";
			CodePhrase mediaType = new CodePhrase("IANA_media-types",
					"text/plain");
			CodePhrase compressionAlgorithm = new CodePhrase(
					"openehr_compression_algorithms", "other");
			// byte[] integrityCheck = new byte[0];
			CodePhrase integrityCheckAlgorithm = new CodePhrase(
					"openehr_integrity_check_algorithms", "SHA-1");
			DvMultimedia thumbnail = null;
			DvURI uri = new DvURI("www.iana.org");
			// byte[] data = new byte[0];
			TerminologyService terminologyService = SimpleTerminologyService
					.getInstance();
			DvMultimedia dm = new DvMultimedia(charset, language,
					alternateText, mediaType, compressionAlgorithm, null,
					integrityCheckAlgorithm, thumbnail, uri, null,
					terminologyService);

			return dm;

		} else if ("DV_COUNT".equals(rmTypeName)) {

			if (valueMap.get(MAGNITUDE) == null) {
				valueMap.put(MAGNITUDE, DEFAULT_COUNT);
			}
		} else if ("DV_DURATION".equals(rmTypeName)) {
			if (valueMap.get(VALUE) == null) {
				return null;
				// valueMap.put(VALUE, DEFAULT_DURATION);
			}
		} else if ("DV_IDENTIFIER".equals(rmTypeName)) {
			// if (valueMap.get(ID) == null) {
			// valueMap.put(ID, DEFAULT_ID);
			// }
			// if (valueMap.get(ASSIGNER) == null) {
			// valueMap.put(ASSIGNER, DEFAULT_ASSIGNER);
			// }
			// if (valueMap.get(ISSUER) == null) {
			// valueMap.put(ISSUER, DEFAULT_ISSUER);
			// }
			// if (valueMap.get(TYPE) == null) {
			// valueMap.put(TYPE, DEFAULT_TYPE);
			// }
		} else if ("DV_PROPORTION".equals(rmTypeName)) {

			// if (!valueMap.containsKey("numerator")
			// || !valueMap.containsKey("denominator")) {
			// valueMap.put("numerator", "0.5");
			// valueMap.put("denominator", "1.0");
			// valueMap.put("precision", "1");
			// valueMap.put(TYPE, ProportionKind.RATIO);
			// }

		}
		Object obj = builder.construct(rmTypeName, valueMap);
		return obj;
	}

	private Object instantiateDvCodedText(Map<String, Object> valueMap,
			Archetype archetype) throws Exception {

		if (valueMap.get(DEFINING_CODE) != null
				&& valueMap.get(DEFINING_CODE) instanceof CodePhrase) {
			CodePhrase codePhrase = (CodePhrase) valueMap.get(DEFINING_CODE);
			String code = codePhrase.getCodeString();

			if (!valueMap.containsKey(VALUE)) {
				String text = null;
				if (isLocallyDefined(codePhrase)) {
					// archetype terms
					text = retrieveArchetypeTermText(code, archetype);
				} else if (isOpenEHRTerm(codePhrase)) {
					// openEHR terms
					text = openEHRTerminology.rubricForCode(code,
							language.getCodeString());
				} else {
					// externally defined term
					text = DEFAULT_CODED_TEXT;
				}
				valueMap.put(VALUE, text);
			}
		} else if (valueMap.get(DEFINING_CODE) != null
				&& valueMap.get(DEFINING_CODE) instanceof DvCodedText) {
			DvCodedText dct = (DvCodedText) valueMap.get(DEFINING_CODE);
			valueMap.put(DEFINING_CODE, dct.getDefiningCode());
			valueMap.put(VALUE, dct.getValue());
		}

		Object obj = builder.construct("DV_CODED_TEXT", valueMap);
		return obj;
	}

	private Object instantiateElement(Map<String, Object> valueMap,
			CComplexObject ccobj, boolean isMapped, boolean isNull)
			throws RMObjectBuildingException {

		if (!ccobj.isRequired() && !isMapped) {
			return null;
		} else if (isNull) {
			valueMap.put(VALUE, null);
			valueMap.put(NULL_FLAVOUR, NULL_FLAVOUR_VALUE_UN);
		} else if (ccobj.isRequired() && !isMapped) {
			valueMap.put(VALUE, null);
			valueMap.put(NULL_FLAVOUR, NULL_FLAVOUR_VALUE_NM);
		}

		Object obj = builder.construct("ELEMENT", valueMap);
		return obj;
	}

	private Object instantiateOtherRMObject(String rmTypeName,
			Map<String, Object> valueMap, Archetype archetype) throws Exception {
		if ("HISTORY".equals(rmTypeName)) {

			if (!valueMap.containsKey(ORIGIN)) {
				valueMap.put(ORIGIN, new DvDateTime());
			}

		} else if ("EVENT".equals(rmTypeName)) {

			if (!valueMap.containsKey(TIME)) {
				valueMap.put(TIME, new DvDateTime());
			}

		} else if ("OBSERVATION".equals(rmTypeName)) {

			addEntryValues(valueMap, archetype);

		} else if ("EVALUATION".equals(rmTypeName)) {

			addEntryValues(valueMap, archetype);

		} else if ("ADMIN_ENTRY".equals(rmTypeName)) {

			addEntryValues(valueMap, archetype);

		} else if ("ACTION".equals(rmTypeName)) {

			addEntryValues(valueMap, archetype);
			if (!valueMap.containsKey(TIME)) {
				if (compositionData.containsKey("time")) {
					valueMap.put(TIME, compositionData.get("time"));
				} else {
					valueMap.put(TIME, new DvDateTime());
				}
			}

		} else if ("INSTRUCTION".equals(rmTypeName)) {

			addEntryValues(valueMap, archetype);

		} else if ("ACTIVITY".equals(rmTypeName)) {

			if (!valueMap.containsKey(TIMING)) {
				valueMap.put(TIMING, new DvParsable("activity timing", "txt"));
			}
			if (!valueMap.containsKey("action_archetype_id")) {
				valueMap.put("action_archetype_id", "string value");
			}
		} else if ("COMPOSITION".equals(rmTypeName)) {

			valueMap.put("composer", party);
			valueMap.put("territory", country);
			valueMap.put("category", category);
			valueMap.put("uid", uid);

			addEntryValues(valueMap, archetype);

		} else if ("EVENT_CONTEXT".equals(rmTypeName)) {

			if (!valueMap.containsKey(START_TIME)) {
				valueMap.put(START_TIME, new DvDateTime());
			}

			if (!valueMap.containsKey(SETTING)) {
				valueMap.put(SETTING, setting);
			}

		} else if ("SECTION".equals(rmTypeName)) {

			List list = (List) valueMap.get("items");
			if (list != null && list.isEmpty()) {
				valueMap.remove("items");
			}

		} else if ("INTERVAL_EVENT".equals(rmTypeName)) {

			if (!valueMap.containsKey("time")) {
				valueMap.put("time", new DvDateTime());
			}

			if (!valueMap.containsKey("width")) {
				valueMap.put("width", new DvDuration("P1d"));
			}

		} else if ("POINT_EVENT".equals(rmTypeName)) {

			if (!valueMap.containsKey("time")) {
				valueMap.put("time", new DvDateTime());
			}
		} else if ("ISM_TRANSITION".equals(rmTypeName)) {

			DvText name = new DvText("Completed");
			DvCodedText careflowStep = new DvCodedText("Completed",
					new CodePhrase("local", "at0043"));
			DvCodedText currentState = new DvCodedText("completed",
					new CodePhrase("openehr", "532"));
			valueMap.put("name", name);
			valueMap.put("careflow_step", careflowStep);
			valueMap.put("current_state", currentState);
		}

		// special fix for event
		if ("EVENT".equals(rmTypeName)) {
			rmTypeName = "POINT_EVENT";
		}

		Object obj = builder.construct(rmTypeName, valueMap);
		return obj;
	}

	/**
	 * Checks if given code_prhase is locally defined within an archetype
	 * 
	 * @param code
	 * @return
	 */
	private boolean isLocallyDefined(CodePhrase code) {
		return "local".equalsIgnoreCase(code.getTerminologyId().toString())
				&& code.getCodeString().startsWith("at");
	}

	/**
	 * Check if code_prhase belongs to openEHR terminology
	 * 
	 * @param code
	 * @return
	 */
	private boolean isOpenEHRTerm(CodePhrase code) {
		return "openehr".equalsIgnoreCase(code.getTerminologyId().getValue());
	}

	/**
	 * Retrieves just the text of given nodeId (at code)
	 * 
	 * @param nodeId
	 * @param archetype
	 * @return
	 * @throws Exception
	 */
	private String retrieveArchetypeTermText(String nodeId, Archetype archetype)
			throws Exception {
		// String termLanguage =
		// archetype.getOriginalLanguage().getCodeString();
		String termLanguage = language.getCodeString();
		ArchetypeTerm term = archetype.getOntology().termDefinition(
				termLanguage, nodeId);
		if (term == null) {
			throw new Exception("term of given code: " + nodeId
					+ ", language: " + language + " not found..");
		}
		return term.getText();
	}

	/**
	 * Retrieves a language-specific name of given nodeId
	 * 
	 * @param nodeId
	 * @param archetype
	 * @return
	 * @throws Exception
	 */
	private DvText retrieveName(String nodeId, Archetype archetype)
			throws Exception {
		DvText name = new DvText(retrieveArchetypeTermText(nodeId, archetype));
		return name;
	}

	/**
	 * Create PartySelf (subject)
	 * 
	 * @return subject
	 * @throws Exception
	 */
	private PartySelf subject() throws Exception {
		PartyRef subject = new PartyRef(new HierObjectID(patientId), "PARTY");
		return new PartySelf(subject);
	}
}