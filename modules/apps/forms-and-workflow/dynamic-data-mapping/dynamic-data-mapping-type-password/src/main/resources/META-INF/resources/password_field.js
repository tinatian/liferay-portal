AUI.add(
	'liferay-ddm-form-field-password',
	function(A) {
		new A.TooltipDelegate(
			{
				position: 'left',
				trigger: '.liferay-ddm-form-field-password .help-icon',
				triggerHideEvent: ['blur', 'mouseleave'],
				triggerShowEvent: ['focus', 'mouseover'],
				visible: false
			}
		);

		var PasswordField = A.Component.create(
			{
				ATTRS: {
					placeholder: {
						value: ''
					},

					tooltip: {
						value: ''
					},

					type: {
						value: 'password'
					}
				},

				EXTENDS: Liferay.DDM.Renderer.Field,

				NAME: 'liferay-ddm-form-field-password',

				prototype: {
					getTemplateContext: function() {
						var instance = this;

						return A.merge(
							PasswordField.superclass.getTemplateContext.apply(instance, arguments),
							{
								placeholder: instance.getLocalizedValue(instance.get('placeholder')),
								tooltip: instance.getLocalizedValue(instance.get('tooltip'))
							}
						);
					},

					_renderErrorMessage: function() {
						var instance = this;

						PasswordField.superclass._renderErrorMessage.apply(instance, arguments);

						var container = instance.get('container');

						var inputGroup = container.one('.input-group-container');

						inputGroup.insert(container.one('.help-block'), 'after');
					},

					_showFeedback: function() {
						var instance = this;

						PasswordField.superclass._showFeedback.apply(instance, arguments);

						var container = instance.get('container');

						var feedback = container.one('.form-control-feedback');

						var inputGroupAddOn = container.one('.input-group-addon');

						if (inputGroupAddOn) {
							feedback.appendTo(inputGroupAddOn);
						}
						else {
							var inputGroupContainer = container.one('.input-group-container');

							inputGroupContainer.placeAfter(feedback);
						}
					}
				}
			}
		);

		Liferay.namespace('DDM.Field').Password = PasswordField;
	},
	'',
	{
		requires: ['aui-tooltip', 'liferay-ddm-form-renderer-field']
	}
);