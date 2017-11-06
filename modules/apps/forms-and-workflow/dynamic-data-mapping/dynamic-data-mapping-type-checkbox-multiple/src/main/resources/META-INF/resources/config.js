;(function() {
	var LiferayAUI = Liferay.AUI;

	AUI().applyConfig(
		{
			groups: {
				'field-checkbox-multiple': {
					base: MODULE_PATH + '/',
					filter: LiferayAUI.getFilterConfig(),
					combine: Liferay.AUI.getCombine(),
					modules: {
						'liferay-ddm-form-field-checkbox-multiple': {
							condition: {
								trigger: 'liferay-ddm-form-renderer'
							},
							path: 'checkbox_multiple_field.js',
							requires: [
								'liferay-ddm-form-renderer-field'
							]
						},
						'liferay-ddm-form-field-checkbox-multiple-template': {
							condition: {
								trigger: 'liferay-ddm-form-renderer'
							},
							path: 'checkbox-multiple.soy.js',
							requires: [
								'soyutils'
							]
						}
					},
					root: MODULE_PATH + '/'
				}
			}
		}
	);
})();