package br.uerj.lampada.openehr.susbuilder.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.uerj.lampada.openehr.susbuilder.utils.Constants;

public abstract class Mapping {

	public static final Map<String, List<String>> nullMap = new HashMap<String, List<String>>();
	public static final Map<String, String> terminologyMap = new HashMap<String, String>();

	public static final Map<String, String> unitMap = new HashMap<String, String>();
	private static final List<String> arCidtr = new ArrayList<String>();

	private static final List<String> arNumc = new ArrayList<String>();
	private static final Map<String, Object> bColumnMap = new HashMap<String, Object>();

	private static final Map<String, Object> cColumnMap = new HashMap<String, Object>();
	private static final Map<String, Object> dColumnMap = new HashMap<String, Object>();

	private static final Map<String, Object> hColumnMap = new HashMap<String, Object>();
	private static final Map<String, Object> mColumnMap = new HashMap<String, Object>();
	private static final Map<String, Object> nColumnMap = new HashMap<String, Object>();

	private static final List<String> nullCID = new ArrayList<String>();
	private static final List<String> nullLINF = new ArrayList<String>();

	private static final List<String> nullMEASURE = new ArrayList<String>();
	private static final Map<String, Object> oColumnMap = new HashMap<String, Object>();
	private static final Map<String, Object> rColumnMap = new HashMap<String, Object>();

	static {
		bColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0009]/value",
						"ap_cmp");
		bColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0011]/value",
						"ap_coduni");
		bColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0028]/value",
						"estado");
		bColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0012]/value",
						"ap_nuidade");
		bColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0010]/value",
						"ap_tpaten");
		bColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0003]/value",
						"ap_motsai");
		bColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0002]/value",
						"ap_dtocor");
		bColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0002.1]/value",
						"ap_cidpri");
		bColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.1]/value",
						"ap_cidsec");
		bColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.2]/value",
						"ap_cidcas");

		// bariatric surgery
		bColumnMap
				.put("/content[openEHR-EHR-OBSERVATION.body_mass_index.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value",
						"ab_imc");
		bColumnMap
				.put("/content[openEHR-EHR-ACTION.procedure-sus.v1]/description[at0001]/items[at0002]/value",
						"ap_pripal");
		bColumnMap.put("time", "ab_dtcirur");
		bColumnMap
				.put("/content[openEHR-EHR-EVALUATION.bariatric_surgery_evaluation.v1]/data[at0001]/items[at0002]/value",
						"ab_mesacom");
		bColumnMap
				.put("/content[openEHR-EHR-EVALUATION.bariatric_surgery_evaluation.v1]/data[at0001]/items[at0003]/value",
						"ab_pontbar");
		bColumnMap
				.put("/content[openEHR-EHR-EVALUATION.bariatric_surgery_evaluation.v1]/data[at0001]/items[at0004]/value",
						"ab_tabbarr");

	}
	static {
		cColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0009]/value",
						"ap_cmp");
		cColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0011]/value",
						"ap_coduni");
		cColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0010]/value",
						"ap_tpaten");
		cColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0003]/value",
						"ap_motsai");
		cColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0002]/value",
						"ap_dtocor");
		cColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0012]/value",
						"ap_nuidade");
		cColumnMap
				.put("/content[openEHR-EHR-ACTION.procedure-sus.v1]/description[at0001]/items[at0002]/value",
						"ap_pripal");
		cColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0028]/value",
						"estado");
		cColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.1]/value",
						"ap_cidsec");
		cColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.2]/value",
						"ap_cidcas");
		cColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0002.1]/value",
						"ap_cidpri");

		// chemotherapy
		cColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0016]/items[at0031]/value",
						"aq_dtintr");
		cColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0016]/items[at0014]/value",
						"aq_esqu");
		cColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.4]/value",
						"aq_linfin");
		cColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0016]/items[at0013]/value",
						"aq_totmpl");
		cColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.tnm_staging-sus.v1]/items[at0.43]/value",
						"aq_cid10");
		cColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.tnm_staging-sus.v1]/items[at0006]/items[at0.44]/value",
						"aq_dtiden");
		cColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.tnm_staging-sus.v1]/items[at0002]/items[at0010]/value",
						"aq_estadi");
		cColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.tnm_staging-sus.v1]/items[at0006]/items[at0035]/value",
						"aq_grahis");
	}

	static {
		// demographic
		dColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.demographic_data.v1]/data[at0001]/items[at0002]/value",
						"nasc");
		dColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.demographic_data.v1]/data[at0001]/items[at0003]/value",
						"ap_sexo");
		dColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.demographic_data.v1]/data[at0001]/items[at0004]/value",
						"ap_racacor");
		dColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.demographic_data.v1]/data[at0001]/items[at0005]/value",
						"ap_etnia");
		dColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.demographic_data.v1]/data[at0001]/items[at0007]/value",
						"instru");
		dColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.demographic_data.v1]/data[at0001]/items[at0008]/value",
						"ap_ufnacio");
	}
	static {
		// hospitalisation
		hColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.admission.v1]/data[at0001]/items[at0071]/value",
						"dt_inter");
		hColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.admission.v1]/data[at0001]/items[at0013]/value",
						"car_int");
		hColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.admission.v1]/data[at0001]/items[at0041]/value",
						"espec");
		hColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.admission.v1]/data[at0001]/items[at0073]/items[at0104]/items[at0106]/value",
						"cgc_hosp");
		hColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.admission.v1]/data[at0001]/items[at0073]/items[at0084]/items[at0087]/value",
						"estado");
		hColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0002]/value",
						"dt_saida");
		hColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0047]/value",
						"cobranca");
		hColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0048]/value",
						"morte");
		hColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.hospitalization_authorization.v1]/data[at0001]/items[at0061]/value",
						"dt_cmpt");
		hColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.hospitalization_authorization.v1]/data[at0001]/items[at0016]/value",
						"uti_mes_to");
		hColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0046]/value",
						"infehosp");
		hColumnMap
				.put("/content[openEHR-EHR-ACTION.procedure-sus.v1]/description[at0001]/items[at0002]/value",
						"proc_rea");
		hColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0002.1]/value",
						"diag_princ");
		hColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.1]/value",
						"diag_secun");
	}

	static {
		mColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0009]/value",
						"ap_cmp");
		mColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0011]/value",
						"ap_coduni");
		mColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0010]/value",
						"ap_tpaten");
		mColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0003]/value",
						"ap_motsai");
		mColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0002]/value",
						"ap_dtocor");
		mColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0012]/value",
						"ap_nuidade");
		mColumnMap
				.put("/content[openEHR-EHR-ACTION.procedure-sus.v1]/description[at0001]/items[at0002]/value",
						"ap_pripal");
		mColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0028]/value",
						"estado");
		mColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.1]/value",
						"ap_cidsec");
		mColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.2]/value",
						"ap_cidcas");
		mColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0002.1]/value",
						"ap_cidpri");

		// medication
		mColumnMap
				.put("/content[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value",
						"am_peso");
		mColumnMap
				.put("/content[openEHR-EHR-OBSERVATION.height.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value",
						"am_altura");
		mColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0018]/items[at0029]/value",
						"am_transpl");
		mColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0018]/items[at0022]/value",
						"am_qtdtran");
	}
	static {
		nColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0009]/value",
						"ap_cmp");
		nColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0011]/value",
						"ap_coduni");
		nColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0028]/value",
						"estado");
		nColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0012]/value",
						"ap_nuidade");
		nColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0010]/value",
						"ap_tpaten");
		nColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0003]/value",
						"ap_motsai");
		nColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0002]/value",
						"ap_dtocor");
		nColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0002.1]/value",
						"ap_cidpri");
		nColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.1]/value",
						"ap_cidsec");
		nColumnMap
				.put("/content[openEHR-EHR-ACTION.procedure-sus.v1]/description[at0001]/items[at0002]/value",
						"ap_pripal");

		// nephrology
		nColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0025]/items[at0030]/value",
						"an_dtpdr");
		nColumnMap
				.put("/content[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value",
						"an_peso");
		nColumnMap
				.put("/content[openEHR-EHR-OBSERVATION.height.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value",
						"an_altura");
		nColumnMap
				.put("/content[openEHR-EHR-OBSERVATION.bodily_output-urination.v1]/data[at0001]/events[at0006]/data[at0003]/items[openEHR-EHR-CLUSTER.fluid.v1]/items[at0006]/items[at0007]/value",
						"an_diures");
		nColumnMap
				.put("/content[openEHR-EHR-OBSERVATION.bodily_output-urination.v1]/data[at0001]/events[at0006]/data[at0003]/items[openEHR-EHR-CLUSTER.fluid.v1]/items[at0001]/value",
						"an_urine");
		nColumnMap
				.put("/content[openEHR-EHR-OBSERVATION.lab_test-blood_glucose.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0078.1]/value",
						"an_glicos");
		nColumnMap
				.put("/content[openEHR-EHR-ACTION.procedure-sus.v1]/description[at0001]/items[at0.63]/value",
						"an_acevas");
		nColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0025]/items[at0026]/value",
						"an_ulsoab");
		nColumnMap
				.put("/content[openEHR-EHR-OBSERVATION.lab_test-urea_and_electrolytes-sus.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0.0.8]/value",
						"an_tru");
		nColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0025]/items[at0027]/value",
						"an_intfis");
		nColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0018]/items[at0021]/value",
						"an_cncdo");
		nColumnMap
				.put("/content[openEHR-EHR-OBSERVATION.lab_test-liver_function.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0078.7]/value",
						"an_albumi");
		nColumnMap
				.put("/content[openEHR-EHR-OBSERVATION.lab_test-antigen_antibody_sus.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0.95]/value",
						"an_hcv");
		nColumnMap
				.put("/content[openEHR-EHR-OBSERVATION.lab_test-antigen_antibody_sus.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0.91]/value",
						"an_hbsag");
		nColumnMap
				.put("/content[openEHR-EHR-OBSERVATION.lab_test-antigen_antibody_sus.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0.94]/value",
						"an_hiv");
		nColumnMap
				.put("/content[openEHR-EHR-OBSERVATION.lab_test-hba1c.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0078.1]/value",
						"an_hb");
	}

	static {
		// outpatient miscellaneous
		oColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0009]/value",
						"ap_cmp");
		oColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0011]/value",
						"ap_coduni");
		oColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0028]/value",
						"estado");
		oColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0012]/value",
						"ap_nuidade");
		oColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0010]/value",
						"ap_tpaten");
		oColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0003]/value",
						"ap_motsai");
		oColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0002]/value",
						"ap_dtocor");
		oColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0002.1]/value",
						"ap_cidpri");
		oColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.1]/value",
						"ap_cidsec");
		oColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.2]/value",
						"ap_cidcas");
		oColumnMap
				.put("/content[openEHR-EHR-ACTION.procedure-sus.v1]/description[at0001]/items[at0002]/value",
						"ap_pripal");
	}
	static {
		arCidtr.add("ar_cidtr1");
		arCidtr.add("ar_cidtr2");
		arCidtr.add("ar_cidtr3");
	}

	static {
		arNumc.add("ar_numc1");
		arNumc.add("ar_numc2");
		arNumc.add("ar_numc3");
	}
	static {
		rColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0009]/value",
						"ap_cmp");
		rColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0011]/value",
						"ap_coduni");
		rColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0028]/value",
						"estado");
		rColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0012]/value",
						"ap_nuidade");
		rColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0010]/value",
						"ap_tpaten");
		rColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0003]/value",
						"ap_motsai");
		rColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0002]/value",
						"ap_dtocor");
		rColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0002.1]/value",
						"ap_cidpri");
		rColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.1]/value",
						"ap_cidsec");
		rColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.2]/value",
						"ap_cidcas");
		rColumnMap
				.put("/content[openEHR-EHR-ACTION.procedure-sus.v1]/description[at0001]/items[at0002]/value",
						"ap_pripal");

		// radiotherapy
		rColumnMap
				.put("/content[openEHR-EHR-ACTION.procedure-sus.v1]/description[at0001]/items[at0014]/value",
						"ar_finali");
		rColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.tnm_staging-sus.v1]/items[at0.43]/value",
						"ar_cid10");
		rColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.4]/value",
						"ar_linfin");
		rColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.tnm_staging-sus.v1]/items[at0002]/items[at0010]/value",
						"ar_estadi");
		rColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.tnm_staging-sus.v1]/items[at0006]/items[at0035]/value",
						"ar_grahis");
		rColumnMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.tnm_staging-sus.v1]/items[at0006]/items[at0.44]/value",
						"ar_dtiden");
		rColumnMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0017]/items[at0032]/value",
						"ar_dtintr");
		rColumnMap
				.put("/content[openEHR-EHR-ACTION.procedure-sus.v1]/description[at0001]/items[at0.59]/value",
						arCidtr);
		rColumnMap
				.put("/content[openEHR-EHR-ACTION.procedure-sus.v1]/description[at0001]/items[at0.61]/value",
						arNumc);
	}

	static {
		terminologyMap
				.put("/content[openEHR-EHR-ACTION.procedure-sus.v1]/description[at0001]/items[at0002]/value",
						"SUS");
		terminologyMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0010]/value",
						"TP_ATEND");
		terminologyMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0011]/value",
						"CNES");
		terminologyMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0023]/items[at0028]/value",
						"IBGE");
		terminologyMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0003]/value",
						"MOTSAIPE");
		terminologyMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.1]/value",
						"CID10");
		terminologyMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.2]/value",
						"CID10");
		terminologyMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0002.1]/value",
						"CID10");

		// bariatrics
		terminologyMap
				.put("/content[openEHR-EHR-EVALUATION.bariatric_surgery_evaluation.v1]/data[at0001]/items[at0003]/value",
						"P_BAROS");
		terminologyMap
				.put("/content[openEHR-EHR-EVALUATION.bariatric_surgery_evaluation.v1]/data[at0001]/items[at0004]/value",
						"TAB_BAROS");

		// chemotherapy
		terminologyMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.tnm_staging-sus.v1]/items[at0.43]/value",
						"CID10");
		terminologyMap
				.put("/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.tnm_staging-sus.v1]/items[at0006]/items[at0035]/value",
						"GRAU_HIS");

		terminologyMap
				.put("/content[openEHR-EHR-ACTION.procedure-sus.v1]/description[at0001]/items[at0014]/value",
						"FINALID");
		terminologyMap
				.put("/content[openEHR-EHR-ACTION.procedure-sus.v1]/description[at0001]/items[at0.59]/value",
						"CID10");

		terminologyMap
				.put("/content[openEHR-EHR-OBSERVATION.lab_test-antigen_antibody_sus.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0.95]/value",
						"POSNEG");
		terminologyMap
				.put("/content[openEHR-EHR-OBSERVATION.lab_test-antigen_antibody_sus.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0.91]/value",
						"POSNEG");
		terminologyMap
				.put("/content[openEHR-EHR-OBSERVATION.lab_test-antigen_antibody_sus.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0.94]/value",
						"POSNEG");

		// demographic
		terminologyMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.demographic_data.v1]/data[at0001]/items[at0003]/value",
						"SEXO");
		terminologyMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.demographic_data.v1]/data[at0001]/items[at0004]/value",
						"RACACOR");
		terminologyMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.demographic_data.v1]/data[at0001]/items[at0008]/value",
						"UFNACIO");
		terminologyMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.demographic_data.v1]/data[at0001]/items[at0007]/value",
						"INSTRU");

		// hospitalisation
		terminologyMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.admission.v1]/data[at0001]/items[at0041]/value",
						"LEITOS");
		terminologyMap
				.put("/content[openEHR-EHR-OBSERVATION.menstruation.v1]/data[at0001]/events[at0002]/state[at0022]/items[at0023]/value",
						"CONTRAC");
		terminologyMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.patient_discharge.v1]/data[at0001]/items[at0047]/value",
						"MOTSAIPE");
		terminologyMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.admission.v1]/data[at0001]/items[at0013]/value",
						"CARATEND");
		terminologyMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.admission.v1]/data[at0001]/items[at0073]/items[at0104]/items[at0106]/value",
						"CGC_HOSP");
		terminologyMap
				.put("/content[openEHR-EHR-ADMIN_ENTRY.admission.v1]/data[at0001]/items[at0073]/items[at0084]/items[at0087]/value",
						"IBGE");
	}
	static {
		unitMap.put(
				"/content[openEHR-EHR-OBSERVATION.body_mass_index.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value",
				"kg/m^2");

		unitMap.put(
				"/content[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value",
				"kg");
		unitMap.put(
				"/content[openEHR-EHR-OBSERVATION.height.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value",
				"cm");

		// chemotherapy
		unitMap.put(
				"/content[openEHR-EHR-ADMIN_ENTRY.high_complexity_procedures_sus.v1]/data[at0001]/items[at0016]/items[at0013]/value",
				"mo");

		// nephrology
		unitMap.put(
				"/content[openEHR-EHR-OBSERVATION.bodily_output-urination.v1]/data[at0001]/events[at0006]/data[at0003]/items[openEHR-EHR-CLUSTER.fluid.v1]/items[at0006]/items[at0007]/value",
				"ml");
		unitMap.put(
				"/content[openEHR-EHR-OBSERVATION.lab_test-blood_glucose.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0078.1]/value",
				"mg/dl");
		unitMap.put(
				"/content[openEHR-EHR-OBSERVATION.lab_test-hba1c.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0078.1]/value",
				"g%");
		unitMap.put(
				"/content[openEHR-EHR-OBSERVATION.lab_test-liver_function.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0078.7]/value",
				"g%");
	}

	static {
		nullMEASURE.add("0");
	}
	static {
		nullCID.add("0000");
	}

	static {
		nullLINF.add("3");
	}
	static {
		nullMap.put(
				"/content[openEHR-EHR-OBSERVATION.height.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value",
				nullMEASURE);
		nullMap.put(
				"/content[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value",
				nullMEASURE);

		nullMap.put(
				"/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.1]/value",
				nullCID);
		nullMap.put(
				"/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.2]/value",
				nullCID);
		nullMap.put(
				"/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0002.1]/value",
				nullCID);

		// radiotherapy
		nullMap.put(
				"/content[openEHR-EHR-ACTION.procedure-sus.v1]/description[at0001]/items[at0.59]/value",
				nullCID);

		// radio and chemotherapy
		nullMap.put(
				"/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.tnm_staging-sus.v1]/items[at0.43]/value",
				nullCID);
		nullMap.put(
				"/content[openEHR-EHR-EVALUATION.problem_diagnosis-sus.v1]/data[at0001]/items[at0.4]/value",
				nullLINF);
	}

	public static Map<String, Object> getColumnMap(String template) {
		Map<String, Object> columnMap = null;
		if (template == null) {
			columnMap = new HashMap<String, Object>();
		} else if (template.equals(Constants.BARIATRIC_SURGERY)) {
			columnMap = bColumnMap;
		} else if (template.equals(Constants.CHEMOTHERAPY)) {
			columnMap = cColumnMap;
		} else if (template.equals(Constants.DEMOGRAPHIC_DATA)) {
			columnMap = dColumnMap;
		} else if (template.equals(Constants.HOSPITALISATION)) {
			columnMap = hColumnMap;
		} else if (template.equals(Constants.MEDICATION)) {
			columnMap = mColumnMap;
		} else if (template.equals(Constants.NEPHROLOGY)) {
			columnMap = nColumnMap;
		} else if (template.equals(Constants.MISCELLANEOUS)) {
			columnMap = oColumnMap;
		} else if (template.equals(Constants.RADIOTHERAPY)) {
			columnMap = rColumnMap;
		}
		return columnMap;
	}

	public static String pathFromColumn(String col, String template) {
		Map<String, Object> columnMap = getColumnMap(template);
		for (String path : columnMap.keySet()) {
			if (columnMap.get(path) instanceof List) {
				for (String mappedCol : (List<String>) (columnMap.get(path))) {
					if (mappedCol.equals(col)) {
						return path;
					}
				}
			} else if (((String) (columnMap.get(path))).equals(col)) {
				return path;
			}
		}
		return null;
	}

}
