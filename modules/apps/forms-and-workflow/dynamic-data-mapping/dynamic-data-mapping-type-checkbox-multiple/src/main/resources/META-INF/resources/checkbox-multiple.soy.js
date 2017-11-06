// This file was automatically generated from checkbox-multiple.soy.
// Please don't edit this file by hand.

/**
 * @fileoverview Templates in namespace ddm.
 * @public
 */

if (typeof ddm == 'undefined') { var ddm = {}; }


ddm.checkbox_multiple = function(opt_data, opt_ignored) {
  var output = '<div class="form-group liferay-ddm-form-field-checkbox-multiple" data-fieldname="' + soy.$$escapeHtmlAttribute(opt_data.name) + '">' + ((opt_data.showLabel) ? '<label for="' + soy.$$escapeHtmlAttribute(opt_data.name) + '">' + soy.$$escapeHtml(opt_data.label) + ((opt_data.required && opt_data.options.length > 1) ? '<span class="icon-asterisk text-warning"></span>' : '') + '</label>' + ((opt_data.tip) ? '<p class="liferay-ddm-form-field-tip">' + soy.$$escapeHtml(opt_data.tip) + '</p>' : '') : '') + '<div class="clearfix checkbox-multiple-options">';
  var optionList93 = opt_data.options;
  var optionListLen93 = optionList93.length;
  for (var optionIndex93 = 0; optionIndex93 < optionListLen93; optionIndex93++) {
    var optionData93 = optionList93[optionIndex93];
    output += ((! opt_data.inline) ? '<div>' : '') + ((opt_data.showAsSwitcher) ? '<label class="checkbox-default' + soy.$$escapeHtmlAttribute(opt_data.inline ? ' checkbox-multiple-switcher-inline' : '') + ' checkbox-option-' + soy.$$escapeHtmlAttribute(optionData93.value) + '" for="' + soy.$$escapeHtmlAttribute(opt_data.name) + '_' + soy.$$escapeHtmlAttribute(optionData93.value) + '"><input ' + soy.$$filterHtmlAttributes(optionData93.status) + ' class="hide toggle-switch " ' + ((opt_data.readOnly) ? 'disabled' : '') + ' id="' + soy.$$escapeHtmlAttribute(opt_data.name) + '_' + soy.$$escapeHtmlAttribute(optionData93.value) + '" name="' + soy.$$escapeHtmlAttribute(opt_data.name) + '" type="checkbox" value="' + soy.$$escapeHtmlAttribute(optionData93.value) + '" /><span aria-hidden="true" class="toggle-switch-bar"><span class="toggle-switch-handle"></span></span><span class="toggle-switch-text toggle-switch-text-right">' + soy.$$escapeHtml(optionData93.label) + ((opt_data.required && opt_data.options.length == 1) ? '<span class="icon-asterisk text-warning"></span>' : '') + '</span></label>' : '<label class="checkbox-default' + soy.$$escapeHtmlAttribute(opt_data.inline ? ' checkbox-inline' : '') + ' checkbox-option-' + soy.$$escapeHtmlAttribute(optionData93.value) + '" for="' + soy.$$escapeHtmlAttribute(opt_data.name) + '_' + soy.$$escapeHtmlAttribute(optionData93.value) + '"><input ' + soy.$$filterHtmlAttributes(optionData93.status) + ' class="field" ' + ((opt_data.dir) ? 'dir="' + soy.$$escapeHtmlAttribute(opt_data.dir) + '"' : '') + ' ' + ((opt_data.readOnly) ? 'disabled' : '') + ' id="' + soy.$$escapeHtmlAttribute(opt_data.name) + '_' + soy.$$escapeHtmlAttribute(optionData93.value) + '" name="' + soy.$$escapeHtmlAttribute(opt_data.name) + '" type="checkbox" value="' + soy.$$escapeHtmlAttribute(optionData93.value) + '" /> ' + soy.$$escapeHtml(optionData93.label) + '</label>' + ((opt_data.required && opt_data.options.length == 1) ? '<span class="icon-asterisk text-warning"></span>' : '')) + ((! opt_data.inline) ? '</div>' : '');
  }
  output += '</div>' + ((opt_data.tip && opt_data.options.length == 1) ? '<p class="liferay-ddm-form-field-tip">' + soy.$$escapeHtml(opt_data.tip) + '</p>' : '') + ((opt_data.childElementsHTML) ? soy.$$filterNoAutoescape(opt_data.childElementsHTML) : '') + '</div>';
  return output;
};
if (goog.DEBUG) {
  ddm.checkbox_multiple.soyTemplateName = 'ddm.checkbox_multiple';
}
