/*
 * component:   "openEHR Reference Implementation"
 * description: "Class TestTerminologyAccess"
 * keywords:    "unit test"
 *
 * author:      "Rong Chen <rong@acode.se>"
 * support:     "Acode HB <support@acode.se>"
 * copyright:   "Copyright (c) 2004 Acode HB, Sweden"
 * license:     "See notice at bottom of class"
 *
 * file:        "$URL: http://svn.openehr.org/ref_impl_java/BRANCHES/Release-1.0/libraries/src/test/org/openehr/rm/support/terminology/TestTerminologyAccess.java $"
 * revision:    "$LastChangedRevision: 2 $"
 * last_change: "$LastChangedDate: 2005-10-12 22:20:08 +0100 (Wed, 12 Oct 2005) $"
 */
package org.openehr.rm.support.terminology;

import java.util.HashSet;
import java.util.Set;

import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.support.identification.TestTerminologyID;

/**
 * TestTerminologyAccess
 * 
 * @author Rong Chen
 * @version 1.0
 */
public class TestTerminologyAccess implements TerminologyAccess {

	public static final CodePhrase ACTIVE = new CodePhrase("test", "active");

	public static final CodePhrase CHANGE = new CodePhrase(
			TestTerminologyID.SNOMEDCT, "changeTypeCode");

	public static final CodePhrase CREATION = new CodePhrase("openehr", "249");

	public static final CodePhrase ENGLISH = new CodePhrase("test", "en");

	public static final CodePhrase EVENT = new CodePhrase("test", "event");

	public static final CodePhrase FUNCTION = new CodePhrase(
			TestTerminologyID.SNOMEDCT, "meanCode");

	public static final CodePhrase LATIN_1 = new CodePhrase("test",
			"iso-8859-1");

	public static final CodePhrase NULL_FLAVOUR = new CodePhrase("test",
			"unanswered");

	public static final CodePhrase PERSISTENT = new CodePhrase("test",
			"persistent");
	public static final CodePhrase RELATIONS = new CodePhrase("test",
			"family_code");
	public static final CodePhrase REVISION = new CodePhrase(
			TestTerminologyID.SNOMEDCT, "revisionCode");
	public static final CodePhrase SETTING = new CodePhrase("test",
			"setting_code");
	public static final CodePhrase SOME_STATE = null;
	public static final CodePhrase SOME_TRANSITION = null;
	static Set<CodePhrase> CODES;
	static {
		CODES = new HashSet<CodePhrase>();
		CODES.add(FUNCTION);
		CODES.add(REVISION);
		CODES.add(EVENT);
		CODES.add(PERSISTENT);
		CODES.add(SETTING);
		CODES.add(CHANGE);
		CODES.add(RELATIONS);
		CODES.add(CREATION);
		CODES.add(ENGLISH);
		CODES.add(ACTIVE);
		CODES.add(NULL_FLAVOUR);
	}

	/**
	 * Returns all codes known in this terminology
	 * 
	 * @return a <code>Set</code> of <code>CodePhrase</code>
	 */
	@Override
	public Set<CodePhrase> allCodes() {
		return null; // todo: implement this method
	}

	/* fields */
	/**
	 * Returns all codes under grouper groupID of this terminology
	 * 
	 * @param groupID
	 * @return Set of CodePhrase for given group ID, empty set returned if not
	 *         found
	 * @throws IllegalArgumentException
	 *             if groupID null or empty
	 */
	@Override
	public Set<CodePhrase> codesForGroupId(String groupID) {
		return null; // todo: implement this method
	}

	/**
	 * Return all codes under grouper whose name of given name and language from
	 * this terminology.
	 * 
	 * @param name
	 * @param language
	 * @return Set of CodePhrase for given group name, empty set returned if not
	 *         found
	 * @throws IllegalArgumentException
	 *             if name,language null or empty
	 */
	@Override
	public Set<CodePhrase> codesForGroupName(String name, String language) {

		return CODES; // todo: implement this method
	}

	/**
	 * Return true if this codeset contains given codePhrase
	 * 
	 * @param code
	 * @return true if has
	 */
	public boolean has(CodePhrase code) {
		return true;
	}

	@Override
	public boolean hasCodeForGroupId(String groupId, CodePhrase code) {
		return true;
	}

	/**
	 * Return true if the code is under grouper of given name and language
	 * 
	 * @param code
	 * @param name
	 * @param language
	 * @return true if the code exists
	 * @throws IllegalArgumentException
	 *             if code or name or language null
	 */
	public boolean hasCodeForGroupName(CodePhrase code, String name,
			String language) {
		return true;
	}

	/**
	 * Returns identification of this TerminologyAccess
	 * 
	 * @return ID not null or empty
	 */
	@Override
	public String id() {
		return null; // todo: implement this method
	}

	/**
	 * Returns all rubric of given code and language
	 * 
	 * @param code
	 * @param language
	 * @return rubric of given code and language or null if not found
	 * @throws IllegalArgumentException
	 *             if code,language null or empty
	 */
	@Override
	public String rubricForCode(String code, String language) {
		return null; // todo: implement this method
	}

}

/*
 * ***** BEGIN LICENSE BLOCK ***** Version: MPL 1.1/GPL 2.0/LGPL 2.1
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the 'License'); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an 'AS IS' basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is TestTerminologyAccess.java
 * 
 * The Initial Developer of the Original Code is Rong Chen. Portions created by
 * the Initial Developer are Copyright (C) 2003-2004 the Initial Developer. All
 * Rights Reserved.
 * 
 * Contributor(s):
 * 
 * Software distributed under the License is distributed on an 'AS IS' basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * ***** END LICENSE BLOCK *****
 */