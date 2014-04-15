package br.uerj.lampada.openehr.susbuilder.mapping;

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
import org.openehr.am.archetype.constraintmodel.CObject;
import org.openehr.am.archetype.constraintmodel.CPrimitiveObject;
import org.openehr.am.archetype.constraintmodel.primitive.CString;

public class PathMapping {

	private static Logger log = Logger.getLogger(PathMapping.class);

	private Map<String, PathMetadata> pathMapping;

	private String template;

	public PathMapping(String template, Archetype archetype,
			Map<String, Archetype> archetypeMap) throws Exception {
		this.template = template;
		this.pathMapping = new HashMap<String, PathMetadata>();
		createMap(archetype.getDefinition(), archetype, archetypeMap, null,
				null);
	}

	private void createArchetypeSlotObject(ArchetypeSlot sobj, Archetype arch,
			Map<String, Archetype> archetypeMap, String archetypeReference)
			throws Exception {

		if (archetypeReference != null) {
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
						createMap(slot.getDefinition(), slot, archetypeMap,
								archetypeReference, "/items[" + march + "]");
					}
				}
			}
		}
	}

	private void createMap(CComplexObject ccobj, Archetype arch,
			Map<String, Archetype> archetypeMap, String archetypeReference,
			String archetypeSlot) throws Exception {

		// root node with archetype_id as node_id
		String nodeId = ccobj.getNodeId();
		if (nodeId != null && nodeId.startsWith("openEHR")) {
			arch = archetypeMap.get(nodeId);
			if (arch == null) {
				throw new Exception("unknown archetype for nodeId: " + nodeId);
			}
			nodeId = arch.getConcept();
		}

		if (ccobj.getAttributes() != null && ccobj.getAttributes().size() > 0) {
			updateMap(ccobj, archetypeReference, archetypeSlot);

			for (CAttribute cattr : ccobj.getAttributes()) {

				updateMap(cattr, archetypeReference, archetypeSlot);

				List<CObject> children = cattr.getChildren();

				if (children != null && children.size() > 0) {
					for (CObject cobj : children) {
						if (cobj instanceof CComplexObject) {
							createMap((CComplexObject) cobj, arch,
									archetypeMap, archetypeReference,
									archetypeSlot);
						} else if (cobj instanceof ArchetypeInternalRef) {
							createArchetypeInternalRefObject(
									(ArchetypeInternalRef) cobj, arch,
									archetypeMap, archetypeSlot);
						} else if (cobj instanceof ArchetypeSlot) {
							createArchetypeSlotObject((ArchetypeSlot) cobj,
									arch, archetypeMap, archetypeReference);
						} else if (cobj instanceof CPrimitiveObject
								|| cobj instanceof CDomainType) {
							updateMap(cobj, archetypeReference, archetypeSlot);
						}
					}
				}
			}
		} else {
			updateMap(ccobj, archetypeReference, archetypeSlot);
		}
	}

	private String splitCamelCase(String s) {
		if (s.matches(".*[a-z].*") && s.matches(".*[A-Z].*")) {
			s = s.replaceAll(String.format("%s|%s|%s",
					"(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
					"(?<=[A-Za-z])(?=[^A-Za-z])"), "_");
		}
		return s.toUpperCase();
	}

	private void updateMap(CAttribute cattr, String archetypeReference,
			String archetypeSlot) {
		String path = getPath(cattr.path(), archetypeReference, archetypeSlot);
		String dataType = splitCamelCase(cattr.getRmAttributeName());
		updateMap(path, dataType);
	}

	private void updateMap(CObject ccobj, String archetypeReference,
			String archetypeSlot) {
		String path = getPath(ccobj.path(), archetypeReference, archetypeSlot);
		String dataType = splitCamelCase(ccobj.getRmTypeName());
		updateMap(path, dataType);
	}

	private void updateMap(String path, String dataType) {
		PathMetadata pm = new PathMetadata(template);
		pm.setColumn(Mapping.getColumnMap(template).get(path));
		pm.setDataType(dataType);
		pm.setNullValues(Mapping.nullMap.get(path));
		pm.setTerminologyName(Mapping.terminologyMap.get(path));
		pm.setUnit(Mapping.unitMap.get(path));
		pathMapping.put(path, pm);
	}

	public void createArchetypeInternalRefObject(ArchetypeInternalRef robj,
			Archetype archetype, Map<String, Archetype> archetypeMap,
			String slotPath) throws Exception {

		log.debug("create archetype internal reference object "
				+ robj.getRmTypeName());

		String pathNodeId = robj.path().replaceAll("\\w+$", "");
		String logicalNodeId = pathNodeId.replaceAll("^.*"
				+ archetype.getArchetypeId().getValue() + ".*?/", "/");
		String targetNodeId = robj.getTargetPath().replaceAll("^/.*/", "");
		String path = logicalNodeId + "" + targetNodeId;
		Object obj = archetype.getPathNodeMap().get(path);

		createMap((CComplexObject) obj, archetype, archetypeMap, robj.path()
				+ "[" + ((CComplexObject) obj).getNodeId() + "]", slotPath);
	}

	public Map<String, PathMetadata> getPathMapping() {
		return pathMapping;
	}

	public void putPathMapping(String path, PathMetadata pm) {
		pathMapping.put(path, pm);
	}
	
	public PathMetadata getPathMetadata(String path) {
		return pathMapping.get(path);
	}

	public Set<String> getPaths() {
		return pathMapping.keySet();
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public static String getPath(String path, String archetypeReference,
			String archetypeSlot) {
		String refPath = null;
		String slotPath = null;

		if (archetypeSlot != null && archetypeReference != null) {
			if (!path.equals("/")) {
				slotPath = archetypeSlot + "" + path;
			} else {
				slotPath = archetypeSlot;
			}
		}
		// handle archetype internal ref object
		if (archetypeReference != null && archetypeSlot == null) {
			String sufPath = "";
			if (!path.equals("/")) {
				String refNodeId = archetypeReference.replaceAll("^.*/", "/");
				refNodeId = refNodeId.replaceAll("\\[", "\\\\[");
				refNodeId = refNodeId.replaceAll("\\]", "\\\\]");
				sufPath = path.replaceAll("^.*" + refNodeId, "");
			}
			refPath = archetypeReference + "" + sufPath;
			path = refPath;
		} else if (archetypeReference != null && archetypeSlot != null) {
			refPath = archetypeReference;
			path = refPath;
		}
		// handle archetype slot object
		if (slotPath != null) {
			if (refPath != null) {
				slotPath = refPath + "" + slotPath;
			}
			path = slotPath;
		}
		// System.out.println(path);
		return path;
	}
}
