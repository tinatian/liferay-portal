AUI.add(
	'liferay-ddl-form-builder-util',
	function(A) {
		var RendererUtil = Liferay.DDM.Renderer.Util;

		var FormBuilderUtil = {
			coerceLanguage: function(value, source, target) {
				if (A.Lang.isArray(value)) {
					value = value.map(
						function(item) {
							return FormBuilderUtil.coerceLanguage(item, source, target);
						}
					);
				}

				if (A.Object.hasKey(value, source)) {
					var newValue = {};

					newValue[target] = value[source];

					value = newValue;
				}

				return value;
			},

			getFieldClass: function(type) {
				var fieldClass = RendererUtil.getFieldClass(type);

				return A.Component.create(
					{
						ATTRS: {
							enableEvaluations: {
								value: false
							}
						},

						AUGMENTS: [Liferay.DDL.FormBuilderSettingsSupport],

						EXTENDS: fieldClass,

						NAME: fieldClass.NAME
					}
				);
			}
		};

		Liferay.namespace('DDL').FormBuilderUtil = FormBuilderUtil;
	},
	'',
	{
		requires: ['liferay-ddl-form-builder-settings-support', 'liferay-ddm-form-renderer-util']
	}
);