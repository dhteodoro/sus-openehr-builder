/**
 * 
 */
package br.uerj.lampada.openehr.susbuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.changecontrol.Contribution;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.composition.CompositionTestBase;
import org.openehr.rm.composition.content.ContentItem;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.ehr.VersionedComposition;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.UIDBasedID;
import org.openehr.rm.support.terminology.TestCodeSetAccess;
import org.openehr.rm.support.terminology.TestTerminologyAccess;
import org.openehr.schemas.v1.CONTRIBUTION;
import org.openehr.schemas.v1.ContributionDocument;
import org.openehr.schemas.v1.EhrDocument;
import org.openehr.schemas.v1.VERSIONEDCOMPOSITION;
import org.openehr.schemas.v1.VERSIONEDEHRACCESS;
import org.openehr.schemas.v1.VERSIONEDEHRSTATUS;
import org.openehr.schemas.v1.VersionedCompositionDocument;
import org.openehr.schemas.v1.VersionedEhrAccessDocument;
import org.openehr.schemas.v1.VersionedEhrStatusDocument;

import br.uerj.lampada.openehr.susbuilder.builder.EHRBuilder;
import br.uerj.lampada.openehr.susbuilder.printer.EHRPrintCore;

/**
 * @author teodoro
 * 
 */
public class EHRConversionTest extends CompositionTestBase {

	private EHRBuilder builder;
	private List<Composition> compositions;
	private XmlOptions xmlOptions;

	/**
	 * @param name
	 * @throws Exception
	 */
	public EHRConversionTest(String name) throws Exception {
		super(name);
	}

	/**
	 * Test method for
	 * {@link br.uerj.lampada.openehr.susbuilder.builder.EHRBuilder#susOpenEHR(java.lang.String, java.lang.String, java.util.List)}
	 * .
	 * 
	 * @throws Exception
	 */
	public void testSusOpenEHR() throws Exception {
		String uuid = "1.2.3.4.5";
		String patientId = "1.1.1." + uuid;
		assertNotNull(builder.getEhr());
		assertEquals(patientId, builder.getEhr().getEhrId().getValue());
	}

	public void testXMLCompositionConversion() throws Exception {

		List<VersionedComposition> vc = builder.getVersionedCompositions();
		for (int i = 0; i < vc.size(); i++) {
			XmlObject xmlObj = EHRPrintCore.getXMLObject(vc.get(i));

			File file = new File("./test/ehr/conversion/composition_" + (i + 1)
					+ ".xml");
			VersionedCompositionDocument doc = VersionedCompositionDocument.Factory
					.newInstance();
			doc.setVersionedComposition((VERSIONEDCOMPOSITION) xmlObj);
			doc.save(file, xmlOptions);

			assertNotNull(xmlObj);
		}
	}

	public void testXMLContributionConversion() throws Exception {

		List<Contribution> vc = builder.getContributions();
		for (int i = 0; i < vc.size(); i++) {
			XmlObject xmlObj = EHRPrintCore.getXMLObject(vc.get(i));

			File file = new File("./test/ehr/conversion/contribution_"
					+ (i + 1) + ".xml");
			ContributionDocument doc = ContributionDocument.Factory
					.newInstance();
			doc.setContribution((CONTRIBUTION) xmlObj);
			doc.save(file, xmlOptions);

			assertNotNull(xmlObj);
		}
	}

	public void testXMLEHRAccessConversion() throws Exception {

		XmlObject xmlObj = EHRPrintCore.getXMLObject(builder
				.getVersionedEHRAccess());

		File file = new File("./test/ehr/conversion/ehrAccess.xml");
		VersionedEhrAccessDocument doc = VersionedEhrAccessDocument.Factory
				.newInstance();
		doc.setVersionedEhrAccess((VERSIONEDEHRACCESS) xmlObj);
		doc.save(file, xmlOptions);

		assertNotNull(xmlObj);
	}

	public void testXMLEHRConversion() throws Exception {

		XmlObject xmlObj = EHRPrintCore.getXMLObject(builder.getEhr());

		File file = new File("./test/ehr/conversion/ehr.xml");
		EhrDocument doc = EhrDocument.Factory.newInstance();
		doc.setEhr((org.openehr.schemas.v1.EHR) xmlObj);
		doc.save(file, xmlOptions);

		assertNotNull(xmlObj);
	}

	public void testXMLEHRStatusConversion() throws Exception {

		XmlObject xmlObj = EHRPrintCore.getXMLObject(builder
				.getVersionedEHRStatus());

		File file = new File("./test/ehr/conversion/ehrStatus.xml");
		VersionedEhrStatusDocument doc = VersionedEhrStatusDocument.Factory
				.newInstance();
		doc.setVersionedEhrStatus((VERSIONEDEHRSTATUS) xmlObj);
		doc.save(file, xmlOptions);

		assertNotNull(xmlObj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		compositions = new ArrayList<Composition>();
		for (int i = 1; i <= 10; i++) {
			DvText name = new DvText("composition");
			UIDBasedID id = new HierObjectID("1.11.2.3.4.5." + i);
			List<ContentItem> content = new ArrayList<ContentItem>();
			content.add(section("section one"));
			content.add(section("section two", "observation"));
			DvCodedText category = TestCodeSetAccess.EVENT;
			Archetyped archetypeDetails = new Archetyped(new ArchetypeID(
					"openehr-ehr_rm-Composition.physical_examination.v2"),
					"1.0");

			Composition composition = new Composition(id, "at0001", name,
					archetypeDetails, null, null, null, content,
					TestTerminologyAccess.ENGLISH, context(), provider(),
					category, territory(), ts);
			compositions.add(composition);
		}

		String uuid = "1.2.3.4.5";
		String patientId = "1.1.1." + uuid;
		builder = new EHRBuilder(patientId, uuid, compositions);
		builder.createEHRObj();

		xmlOptions = new XmlOptions();
		xmlOptions.setSavePrettyPrint();
		xmlOptions.setSaveAggressiveNamespaces();
		xmlOptions.setSaveInner();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
