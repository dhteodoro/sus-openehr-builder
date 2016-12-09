package br.uerj.lampada.openehr.susbuilder.builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.changecontrol.Contribution;
import org.openehr.rm.common.changecontrol.OriginalVersion;
import org.openehr.rm.common.changecontrol.Version;
import org.openehr.rm.common.generic.AuditDetails;
import org.openehr.rm.common.generic.PartyIdentified;
import org.openehr.rm.common.generic.PartySelf;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.ehr.EHRAccess;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.ehr.VersionedComposition;
import org.openehr.rm.ehr.VersionedEHRAccess;
import org.openehr.rm.ehr.VersionedEHRStatus;
import org.openehr.rm.security.AccessControlSettings;
import org.openehr.rm.security.BasicAccessControlSettings;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectID;
import org.openehr.rm.support.identification.ObjectRef;
import org.openehr.rm.support.identification.ObjectVersionID;
import org.openehr.rm.support.identification.PartyRef;
import org.openehr.rm.support.identification.UIDBasedID;
import org.openehr.terminology.SimpleTerminologyService;

import br.uerj.lampada.openehr.susbuilder.utils.Constants;

/**
 * This class should be used to create a versioned patient EHRs.
 * 
 * UUID prefixes: EHR: 1.1.1 Composition: 1.2.x Contribution: 1.3.x EHRAccess:
 * 1.4.1 EHRStatus: 1.5.1
 * 
 * TODO:
 * 
 * @author Teodoro
 */
public class EHRBuilder {

	private static PartyIdentified COMMITTER = new PartyIdentified(null,
			"UERJ SUS OpenEHR Builder", null);

	private static String NAMESPACE = "local";

	private static String SYSTEM_ID = "susehrbuilder.openehr.lampada.uerj.br";

	// VERSIONED_COMPOSITION
	private List<ObjectRef> compositionRefs;

	// Compositions
	private List<Composition> compositions;

	// VERSIONED_CONTRIBUTION
	private List<ObjectRef> contributionRefs;

	// Contributions
	private List<Contribution> contributions;

	private int count;

	// EHR
	private EHR ehr;

	// VERSIONED_EHR_ACCESS
	private ObjectRef ehrAccessRefs;

	// VERSIONED_EHR_STATUS
	private ObjectRef ehrStatusRefs;

	private ObjectRef ownerID;

	private String patientId;

	private String uuid;

	// Versioned Compositions
	private List<VersionedComposition> versionedCompositions;

	// Versioned EHR Access
	private VersionedEHRAccess versionedEHRAccess;

	// Versioned EHR Status
	private VersionedEHRStatus versionedEHRStatus;

	public EHRBuilder(String patientId, String uuid,
			List<Composition> compositions) throws Exception {
		this.patientId = patientId;
		this.uuid = uuid;
		this.compositions = compositions;
		this.ownerID = new ObjectRef(new HierObjectID(patientId), NAMESPACE,
				"EHR");
		this.count = 1;

		this.versionedCompositions = new ArrayList<VersionedComposition>();
		this.contributions = new ArrayList<Contribution>();

		this.compositionRefs = new ArrayList<ObjectRef>();
		this.contributionRefs = new ArrayList<ObjectRef>();
	}

	public void createEHRObj() throws Exception {
		createCompositions();
		createEHRAccess();
		createEHRStatus();
		createEHR();
	}

	public List<Contribution> getContributions() {
		return contributions;
	}

	// Start POJO
	public EHR getEhr() {
		return ehr;
	}

	public List<VersionedComposition> getVersionedCompositions() {
		return versionedCompositions;
	}

	public VersionedEHRAccess getVersionedEHRAccess() {
		return versionedEHRAccess;
	}

	public VersionedEHRStatus getVersionedEHRStatus() {
		return versionedEHRStatus;
	}

	public List<Version<Composition>> getVersionFromComposition()
			throws Exception {
		// create Compositions
		List<Version<Composition>> versions = new ArrayList<Version<Composition>>();
		for (Composition composition : compositions) {
			String compositionUUID = Constants.COMPOSITION_UUID_PREFIX + count
					+ "." + uuid;
			String contributionUUID = Constants.CONTRIBUTION_UUID_PREFIX
					+ count + "." + uuid;

			ObjectRef contributionRef = new ObjectRef(new HierObjectID(
					contributionUUID), NAMESPACE, "CONTRIBUTION");

			Version<Composition> vc = originalVersion(compositionUUID,
					composition, contributionRef, "composition");

			versions.add(vc);
			count++;
		}
		return versions;
	}

	public void setContributions(List<Contribution> contributions) {
		this.contributions = contributions;
	}

	public void setEhr(EHR ehr) {
		this.ehr = ehr;
	}

	public void setVersionedCompositions(
			List<VersionedComposition> versionedCompositions) {
		this.versionedCompositions = versionedCompositions;
	}

	// private static boolean CREATE_SEPARATE_CONTRIBUTION_FOR_EACH_FILE = true;

	public void setVersionedEHRAccess(VersionedEHRAccess versionedEHRAccess) {
		this.versionedEHRAccess = versionedEHRAccess;
	}

	public void setVersionedEHRStatus(VersionedEHRStatus versionedEHRStatus) {
		this.versionedEHRStatus = versionedEHRStatus;
	}

	private AuditDetails constructAuditDetails(String type) throws Exception {
		// ** Construct audit details for the contribution as a whole: **
		DvDateTime timeCommitted = new DvDateTime();
		DvCodedText changeType = new DvCodedText("creation", new CodePhrase(
				"openehr", "249"));
		String descriptionText = "creation of " + type;
		DvText description = new DvText(descriptionText);

		return new AuditDetails(SYSTEM_ID, COMMITTER, timeCommitted,
				changeType, description, SimpleTerminologyService.getInstance());
	}

	private Contribution contribution(String uuid, Set<ObjectRef> versions,
			String type) throws Exception {
		// each contribution should have a distinct uid
		ObjectID uid = new HierObjectID(uuid);
		AuditDetails audit = constructAuditDetails(type);

		return new Contribution(uid, versions, audit);
	}

	private void createCompositions() throws Exception {
		// create Compositions
		for (Composition composition : compositions) {
			String compositionUUID = Constants.COMPOSITION_UUID_PREFIX + count
					+ "." + uuid;
			String contributionUUID = Constants.CONTRIBUTION_UUID_PREFIX
					+ count + "." + uuid;

			ObjectRef contributionRef = new ObjectRef(new HierObjectID(
					contributionUUID), NAMESPACE, "CONTRIBUTION");

			VersionedComposition vc = versionedComposition(compositionUUID,
					composition, ownerID, contributionRef);
			versionedCompositions.add(vc);
			compositionRefs.add(new ObjectRef(vc.getUid(), NAMESPACE,
					"VERSIONED_COMPOSITION"));

			Set<ObjectRef> contRefs = new HashSet<ObjectRef>();
			contRefs.add(new ObjectRef(vc.allVersionIds().get(0), NAMESPACE,
					"VERSIONED_COMPOSITION"));
			Contribution c = contribution(contributionUUID, contRefs,
					"composition");
			contributions.add(c);
			contributionRefs.add(new ObjectRef(c.getUid(), NAMESPACE,
					"CONTRIBUTION"));
			count++;
		}
	}

	private void createEHR() {
		HierObjectID systemId = new HierObjectID(SYSTEM_ID);
		HierObjectID ehrId = new HierObjectID(patientId);
		DvDateTime timeCreated = new DvDateTime();

		setEhr(new EHR(systemId, ehrId, timeCreated, contributionRefs,
				ehrAccessRefs, ehrStatusRefs, null, compositionRefs));
	}

	private void createEHRAccess() throws Exception {
		// create Contributions of EHRAccess
		String contributionUUID = Constants.CONTRIBUTION_UUID_PREFIX + count
				+ "." + uuid;
		ObjectRef contributionRef = new ObjectRef(new HierObjectID(
				contributionUUID), NAMESPACE, "CONTRIBUTION");

		String ehrAccessUUID = Constants.EHRACCESS_UUID_PREFIX + "." + uuid;
		versionedEHRAccess = versionedEHRAccess(ehrAccessUUID,
				ehrAccess(ehrAccessUUID), ownerID, contributionRef);
		ehrAccessRefs = new ObjectRef(versionedEHRAccess.getUid(), NAMESPACE,
				"VERSIONED_EHR_ACCESS");

		Set<ObjectRef> contRefs = new HashSet<ObjectRef>();
		contRefs.add(new ObjectRef(versionedEHRAccess.allVersionIds().get(0),
				NAMESPACE, "VERSIONED_EHR_ACCESS"));
		contributions
				.add(contribution(contributionUUID, contRefs, "ehr access"));
		contributionRefs.add(contributionRef);
		count++;
	}

	private void createEHRStatus() throws Exception {
		// create Contributions of EHRStatus
		String contributionUUID = Constants.CONTRIBUTION_UUID_PREFIX + count
				+ "." + uuid;
		ObjectRef contributionRef = new ObjectRef(new HierObjectID(
				contributionUUID), NAMESPACE, "CONTRIBUTION");

		String ehrStatusUUID = Constants.EHRSTATUS_UUID_PREFIX + "." + uuid;
		versionedEHRStatus = versionedEHRStatus(ehrStatusUUID,
				ehrStatus(ehrStatusUUID), ownerID, contributionRef);
		ehrStatusRefs = new ObjectRef(versionedEHRStatus.getUid(), NAMESPACE,
				"VERSIONED_EHR_STATUS");

		Set<ObjectRef> contRefs = new HashSet<ObjectRef>();
		contRefs.add(new ObjectRef(versionedEHRStatus.allVersionIds().get(0),
				NAMESPACE, "VERSIONED_EHR_STATUS"));
		contributions
				.add(contribution(contributionUUID, contRefs, "ehr status"));
		contributionRefs.add(contributionRef);
	}

	private EHRAccess ehrAccess(String uuid) throws Exception {
		// each EHRAccess should have a distinct uid
		UIDBasedID uid = new ObjectVersionID(uuid, SYSTEM_ID, "1");
		String archetypeNodeId = "openehr-EHR-ITEM_TREE.ehraccess-susbuilder.v1";
		DvText name = new DvText("EHR Access");
		AccessControlSettings settings = new BasicAccessControlSettings(true);

		return new EHRAccess(uid, archetypeNodeId, name, null, null, null,
				null, settings);
	}

	private EHRStatus ehrStatus(String uuid) throws Exception {
		// each EHRStatus should have a distinct uid
		UIDBasedID uid = new ObjectVersionID(uuid, SYSTEM_ID, "1");
		String archetypeNodeId = "openehr-EHR-ITEM_TREE.ehrstatus-susbuilder.v1";
		DvText name = new DvText("EHR Status");

		Archetyped archetypeDetails = new Archetyped(new ArchetypeID(
				"openehr-EHR-ITEM_TREE.ehrstatus-susbuilder.v1"), compositions
				.get(0).getArchetypeDetails().getRmVersion());
		PartySelf subject = new PartySelf(new PartyRef(new HierObjectID(
				patientId), "PARTY"));

		boolean isQueryable = true;
		boolean isModifiable = false;

		return new EHRStatus(uid, archetypeNodeId, name, archetypeDetails,
				null, null, null, subject, isQueryable, isModifiable, null);
	}

	private <T> Version originalVersion(String uuid, T item,
			ObjectRef contribution, String type) throws Exception {
		// each composition should have a distinct uid
		ObjectVersionID uid = new ObjectVersionID(uuid, SYSTEM_ID, "1");
		DvCodedText lifecycleState = new DvCodedText("complete",
				new CodePhrase("openehr", "532"));
		AuditDetails commitAudit = constructAuditDetails(type);

		return new OriginalVersion(uid, null, item, lifecycleState,
				commitAudit, contribution, null, null, null);
	}

	private VersionedComposition versionedComposition(String uuid,
			Composition composition, ObjectRef ownerID, ObjectRef contribution)
			throws Exception {
		// each composition should have a distinct uid
		HierObjectID uid = new HierObjectID(uuid);
		DvDateTime timeCreated = new DvDateTime();
		Version<Composition> item = originalVersion(uuid, composition,
				contribution, "composition");
		Set<Version<Composition>> versions = new HashSet<Version<Composition>>();
		versions.add(item);
		return new VersionedComposition(uid, ownerID, timeCreated, versions);
	}

	private VersionedEHRAccess versionedEHRAccess(String uuid,
			EHRAccess ehrAccess, ObjectRef ownerID, ObjectRef contribution)
			throws Exception {
		// each composition should have a distinct uid
		HierObjectID uid = new HierObjectID(uuid);
		DvDateTime timeCreated = new DvDateTime();
		Version<EHRAccess> item = originalVersion(uuid, ehrAccess,
				contribution, "ehrAccess");
		Set<Version<EHRAccess>> versions = new HashSet<Version<EHRAccess>>();
		versions.add(item);

		return new VersionedEHRAccess(uid, ownerID, timeCreated, versions);
	}

	private VersionedEHRStatus versionedEHRStatus(String uuid,
			EHRStatus ehrStatus, ObjectRef ownerID, ObjectRef contribution)
			throws Exception {
		// each composition should have a distinct uid
		HierObjectID uid = new HierObjectID(uuid);
		DvDateTime timeCreated = new DvDateTime();
		Version<EHRStatus> item = originalVersion(uuid, ehrStatus,
				contribution, "ehrStatus");
		Set<Version<EHRStatus>> versions = new HashSet<Version<EHRStatus>>();
		versions.add(item);

		return new VersionedEHRStatus(uid, ownerID, timeCreated, versions);
	}
}