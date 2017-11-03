AUI.add(
	'liferay-ddm-form-field-checkbox-multiple',
	function(A) {
		var Lang = A.Lang;

		var TPL_OPTION = '<option>{label}</option>';

		var CheckboxMultipleField = A.Component.create(
			{
				ATTRS: {
					inline: {
						value: true
					},

					options: {
						validator: Array.isArray,
						value: []
					},

					showAsSwitcher: {
						value: false
					},

					type: {
						value: 'checkbox_multiple'
					}
				},

				EXTENDS: Liferay.DDM.Renderer.Field,

				NAME: 'liferay-ddm-form-field-checkbox-multiple',

				prototype: {
					getOptions: function() {
						var instance = this;

						var options = instance.get('options');

						return A.map(
							instance.get('options'),
							function(item) {
								return {
									label: item.label[instance.get('locale')],
									status: instance._getOptionStatus(item),
									value: item.value
								};
							}
						);
					},

					getTemplateContext: function() {
						var instance = this;

						return A.merge(
							CheckboxMultipleField.superclass.getTemplateContext.apply(instance, arguments),
							{
								inline: instance.get('inline'),
								options: instance.getOptions(),
								showAsSwitcher: instance.get('showAsSwitcher')
							}
						);
					},

					getValue: function() {
						var instance = this;

						var container = instance.get('container');

						var values = [];

						if (container) {
							container.all(instance.getInputSelector()).each(
								function(optionNode) {
									var isChecked = !!optionNode.attr('checked');

									if (isChecked) {
										values.push(optionNode.val());
									}
								}
							);
						}

						return values.join();
					},

					setValue: function(val) {
						var instance = this;

						var container = instance.get('container');

						var checkboxesNodeList = container.all('input[type="checkbox"]');

						checkboxesNodeList.removeAttribute('checked');

						var checkboxCheck = checkboxesNodeList.filter(
							function(node) {
								return node.val() === value;
							}
						).item(0);

						if (checkboxCheck) {
							checkboxCheck.attr('checked', true);

							instance.fire(
								'valueChanged',
								{
									field: instance,
									value: value
								}
							);
						}
					},

					_getOptionStatus: function(option) {
						var instance = this;

						var status = '';

						var value = instance.getValue();

						if (value.indexOf(option.value) > -1) {
							status = 'checked';
						}

						return status;
					},

					_renderErrorMessage: function() {
						var instance = this;

						var container = instance.get('container');

						CheckboxMultipleField.superclass._renderErrorMessage.apply(instance, arguments);

						container.all('.help-block').appendTo(container);
					}
				}
			}
		);

		Liferay.namespace('DDM.Field').CheckboxMultiple = CheckboxMultipleField;
	},
	'',
	{
		requires: ['liferay-ddm-form-renderer-field']
	}
);