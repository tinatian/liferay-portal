/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.journal.transformer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalTransformerListenerRegistry;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.templateparser.TransformerListener;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marcellus Tavares
 */
@RunWith(Arquillian.class)
public class JournalTransformerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_ddmStructure = DDMStructureTestUtil.addStructure(
			JournalArticle.class.getName());
	}

	@Test
	public void testContentTransformerListener() throws Exception {
		DDMForm ddmForm = new DDMForm();

		ddmForm.addAvailableLocale(LocaleUtil.US);
		ddmForm.addDDMFormField(
			DDMFormTestUtil.createTextDDMFormField(
				"link", false, false, false));
		ddmForm.addDDMFormField(
			DDMFormTestUtil.createTextDDMFormField(
				"name", false, false, false));
		ddmForm.setDefaultLocale(LocaleUtil.US);

		_ddmStructure = DDMStructureTestUtil.addStructure(
			JournalArticle.class.getName(), ddmForm);

		String xsl = "$name.getData()";

		_ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_VM, xsl);

		String xml = DDMStructureTestUtil.getSampleStructuredContent(
			"Joe Bloggs");

		_article = JournalTestUtil.addArticleWithXMLContent(
			xml, _ddmStructure.getStructureKey(),
			_ddmTemplate.getTemplateKey());

		String content = JournalArticleLocalServiceUtil.getArticleContent(
			_article, _ddmTemplate.getTemplateKey(), Constants.VIEW, "en_US",
			null, null);

		Assert.assertEquals("Joe Bloggs", content);

		Document document = UnsecureSAXReaderUtil.read(xml);

		Element element = (Element)document.selectSingleNode(
			"//dynamic-content");

		element.setText("[@" + _article.getArticleId() + ";name@]");

		content = JournalArticleLocalServiceUtil.getArticleContent(
			_article, _ddmTemplate.getTemplateKey(), Constants.VIEW, "en_US",
			null, null);

		Assert.assertEquals("Joe Bloggs", content);
	}

	@Test
	public void testFTLTransformation() throws Exception {
		String xml = DDMStructureTestUtil.getSampleStructuredContent(
			"name", "Joe Bloggs");

		String script = "${name.getData()} - ${viewMode}";

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_FTL, script);

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				xml, _ddmStructure.getStructureKey(),
				ddmTemplate.getTemplateKey());

		String content = JournalArticleLocalServiceUtil.getArticleContent(
			journalArticle, ddmTemplate.getTemplateKey(), Constants.PRINT,
			"en_US", null, null);

		Assert.assertEquals("Joe Bloggs - print", content);
	}

	@Test
	public void testLocaleTransformerListener() throws Exception {
		Map<Locale, String> contents = new HashMap<>();

		contents.put(LocaleUtil.BRAZIL, "Joao da Silva");
		contents.put(LocaleUtil.US, "Joe Bloggs");

		String xml = DDMStructureTestUtil.getSampleStructuredContent(
			contents, LanguageUtil.getLanguageId(LocaleUtil.US));

		String script = "$name.getData()";

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_VM, script);

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				xml, _ddmStructure.getStructureKey(),
				ddmTemplate.getTemplateKey());

		String content = JournalArticleLocalServiceUtil.getArticleContent(
			journalArticle, ddmTemplate.getTemplateKey(), Constants.VIEW,
			"en_US", null, null);

		Assert.assertEquals("Joe Bloggs", content);

		content = JournalArticleLocalServiceUtil.getArticleContent(
			journalArticle, ddmTemplate.getTemplateKey(), Constants.VIEW,
			"pt_BR", null, null);

		Assert.assertEquals("Joao da Silva", content);

		content = JournalArticleLocalServiceUtil.getArticleContent(
			journalArticle, ddmTemplate.getTemplateKey(), Constants.VIEW,
			"fr_CA", null, null);

		Assert.assertEquals("Joe Bloggs", content);
	}

	@Test
	public void testLocaleTransformerListenerNestedFieldWithNoTranslation()
		throws Exception {

		Map<Locale, String> contents = new HashMap<>();

		contents.put(LocaleUtil.US, "Joe Bloggs");

		String xml = DDMStructureTestUtil.getSampleStructuredContent(
			contents, LanguageUtil.getLanguageId(LocaleUtil.US));

		Document document = UnsecureSAXReaderUtil.read(xml);

		Element rootElement = document.getRootElement();

		Attribute availableLocalesAttribute = rootElement.attribute(
			"available-locales");

		availableLocalesAttribute.setValue("en_US,pt_BR");

		Element dynamicElement = (Element)document.selectSingleNode(
			"//dynamic-element");

		dynamicElement.addElement("nestedElement");

		String script = "$name.getData()";

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_VM, script);

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				xml, _ddmStructure.getStructureKey(),
				ddmTemplate.getTemplateKey());

		String content = JournalArticleLocalServiceUtil.getArticleContent(
			journalArticle, ddmTemplate.getTemplateKey(), Constants.VIEW,
			"en_US", null, null);

		Assert.assertEquals("Joe Bloggs", content);

		content = JournalArticleLocalServiceUtil.getArticleContent(
			journalArticle, ddmTemplate.getTemplateKey(), Constants.VIEW,
			"pt_BR", null, null);

		Assert.assertEquals("Joe Bloggs", content);
	}

	@Test
	public void testRegexTransformerListener() throws Exception {
		initRegexTransformerListener();

		String xml = DDMStructureTestUtil.getSampleStructuredContent(
			"name", "Joe Bloggs");

		String script = "Hello $name.getData(), Welcome to beta.sample.com.";

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_VM, script);

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				xml, _ddmStructure.getStructureKey(),
				ddmTemplate.getTemplateKey());

		String content = JournalArticleLocalServiceUtil.getArticleContent(
			journalArticle, ddmTemplate.getTemplateKey(), Constants.VIEW,
			"en_US", null, null);

		Assert.assertEquals(
			"Hello Joe Bloggs, Welcome to production.sample.com.", content);
	}

	@Test
	public void testTokensTransformerListener() throws Exception {
		String xml = DDMStructureTestUtil.getSampleStructuredContent();

		String script = "@company_id@";

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_VM, script);

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				xml, _ddmStructure.getStructureKey(),
				ddmTemplate.getTemplateKey());

		String content = JournalArticleLocalServiceUtil.getArticleContent(
			journalArticle, ddmTemplate.getTemplateKey(), Constants.VIEW,
			"en_US", null, null);

		Assert.assertEquals(
			String.valueOf(TestPropsValues.getCompanyId()), content);

		script = "@@company_id@@";

		ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_VM, script);

		content = JournalArticleLocalServiceUtil.getArticleContent(
			journalArticle, ddmTemplate.getTemplateKey(), Constants.VIEW,
			"en_US", null, null);

		Assert.assertEquals(
			String.valueOf(TestPropsValues.getCompanyId()), content);
	}

	@Test
	public void testViewCounterTransformerListener() throws Exception {
		String xml = DDMStructureTestUtil.getSampleStructuredContent();

		String script = "@view_counter@";

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_VM, script);

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				xml, _ddmStructure.getStructureKey(),
				ddmTemplate.getTemplateKey());

		journalArticle.setResourcePrimKey(1);

		String content = JournalArticleLocalServiceUtil.getArticleContent(
			journalArticle, ddmTemplate.getTemplateKey(), Constants.VIEW,
			"en_US", null, null);

		StringBundler sb = new StringBundler(5);

		sb.append("<script type=\"text/javascript\">");
		sb.append("Liferay.Service('/assetentry/increment-view-counter',");
		sb.append("{userId:0, className:'");
		sb.append("com.liferay.journal.model.JournalArticle', classPK:1});");
		sb.append("</script>");

		Assert.assertEquals(sb.toString(), content);
	}

	@Test
	public void testVMTransformation() throws Exception {
		_ddmStructure = DDMStructureTestUtil.addStructure(
			TestPropsValues.getGroupId(), JournalArticle.class.getName());

		_ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_VM, "$name.getData()");

		String xml = DDMStructureTestUtil.getSampleStructuredContent(
			"name", "Joe Bloggs");

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_VM,
			"#parse(\"$templatesPath/" + _ddmTemplate.getTemplateKey() + "\")");

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				xml, _ddmStructure.getStructureKey(),
				ddmTemplate.getTemplateKey());

		String content = JournalArticleLocalServiceUtil.getArticleContent(
			journalArticle, ddmTemplate.getTemplateKey(), Constants.VIEW,
			"en_US", null, null);

		Assert.assertEquals("Joe Bloggs", content);

		ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_VM,
			"#parse(\"$journalTemplatesPath/" + _ddmTemplate.getTemplateKey() +
				"\")");

		content = JournalArticleLocalServiceUtil.getArticleContent(
			journalArticle, ddmTemplate.getTemplateKey(), Constants.VIEW,
			"en_US", null, null);

		Assert.assertEquals("Joe Bloggs", content);
	}

	protected void initRegexTransformerListener() {
		TransformerListener transformerListener =
			_journalTransformerListenerRegistry.getTransformerListener(
				"com.liferay.journal.transformer.RegexTransformerListener");

		CacheRegistryUtil.setActive(true);

		List<Pattern> patterns = new ArrayList<>();
		List<String> replacements = new ArrayList<>();

		for (int i = 0; i < 100; i++) {
			String regex = TestPropsUtil.get(
				"journal.transformer.regex.pattern." + i);
			String replacement = TestPropsUtil.get(
				"journal.transformer.regex.replacement." + i);

			if (Validator.isNull(regex) || Validator.isNull(replacement)) {
				break;
			}

			patterns.add(Pattern.compile(regex));
			replacements.add(replacement);
		}

		ReflectionTestUtil.setFieldValue(
			transformerListener, "_patterns", patterns);
		ReflectionTestUtil.setFieldValue(
			transformerListener, "_replacements", replacements);
	}

	@DeleteAfterTestRun
	private JournalArticle _article;

	@DeleteAfterTestRun
	private DDMStructure _ddmStructure;

	@DeleteAfterTestRun
	private DDMTemplate _ddmTemplate;

	@Inject
	private JournalTransformerListenerRegistry
		_journalTransformerListenerRegistry;

}