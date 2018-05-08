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

package com.liferay.portal.spring.aop;

import com.liferay.portal.kernel.util.ArrayUtil;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Shuyang Zhou
 */
public class ServiceBeanAopCacheManager {

	public static <T> T getAnnotation(
		MethodInvocation methodInvocation,
		Class<? extends Annotation> annotationType, T defaultValue) {

		Annotation[] annotations = _annotations.get(
			methodInvocation.getMethod());

		if (annotations == _nullAnnotations) {
			return defaultValue;
		}

		if (annotations == null) {
			return null;
		}

		for (Annotation annotation : annotations) {
			if (annotation.annotationType() == annotationType) {
				return (T)annotation;
			}
		}

		return defaultValue;
	}

	public static void putAnnotations(
		MethodInvocation methodInvocation, Annotation[] annotations) {

		if (ArrayUtil.isEmpty(annotations)) {
			annotations = _nullAnnotations;
		}

		_annotations.put(methodInvocation.getMethod(), annotations);
	}

	public ServiceBeanAopCacheManager(
		List<MethodInterceptor> fullMethodInterceptors) {

		ArrayList<MethodInterceptor> classLevelMethodInterceptors =
			new ArrayList<>();

		for (MethodInterceptor methodInterceptor : fullMethodInterceptors) {
			if (methodInterceptor instanceof AnnotationChainableMethodAdvice) {
				AnnotationChainableMethodAdvice<?>
					annotationChainableMethodAdvice =
						(AnnotationChainableMethodAdvice<?>)methodInterceptor;

				Class<? extends Annotation> annotationClass =
					annotationChainableMethodAdvice.getAnnotationClass();

				Target target = annotationClass.getAnnotation(Target.class);

				if (target == null) {
					classLevelMethodInterceptors.add(methodInterceptor);
				}
				else {
					for (ElementType elementType : target.value()) {
						if (elementType == ElementType.TYPE) {
							classLevelMethodInterceptors.add(methodInterceptor);

							break;
						}
					}
				}
			}
			else {
				classLevelMethodInterceptors.add(methodInterceptor);
			}
		}

		classLevelMethodInterceptors.trimToSize();

		_classLevelMethodInterceptors = classLevelMethodInterceptors;
		_fullMethodInterceptors = fullMethodInterceptors;
	}

	public List<MethodInterceptor> getMethodInterceptors(
		MethodInvocation methodInvocation) {

		List<MethodInterceptor> methodInterceptors = _methodInterceptors.get(
			methodInvocation.getMethod());

		if (methodInterceptors == null) {
			methodInterceptors = new ArrayList<>(_fullMethodInterceptors);

			_methodInterceptors.put(
				methodInvocation.getMethod(), methodInterceptors);
		}

		return methodInterceptors;
	}

	public Map
		<Class<? extends Annotation>, AnnotationChainableMethodAdvice<?>[]>
			getRegisteredAnnotationChainableMethodAdvices() {

		return _annotationChainableMethodAdvices;
	}

	public boolean isRegisteredAnnotationClass(
		Class<? extends Annotation> annotationClass) {

		return _annotationChainableMethodAdvices.containsKey(annotationClass);
	}

	public void putMethodInterceptors(
		MethodInvocation methodInvocation,
		List<MethodInterceptor> methodInterceptors) {

		_methodInterceptors.put(
			methodInvocation.getMethod(), methodInterceptors);
	}

	public void registerAnnotationChainableMethodAdvice(
		Class<? extends Annotation> annotationClass,
		AnnotationChainableMethodAdvice<?> annotationChainableMethodAdvice) {

		AnnotationChainableMethodAdvice<?>[] annotationChainableMethodAdvices =
			_annotationChainableMethodAdvices.get(annotationClass);

		if (annotationChainableMethodAdvices == null) {
			annotationChainableMethodAdvices =
				new AnnotationChainableMethodAdvice<?>[1];

			annotationChainableMethodAdvices[0] =
				annotationChainableMethodAdvice;
		}
		else {
			annotationChainableMethodAdvices = ArrayUtil.append(
				annotationChainableMethodAdvices,
				annotationChainableMethodAdvice);
		}

		_annotationChainableMethodAdvices.put(
			annotationClass, annotationChainableMethodAdvices);
	}

	public void removeMethodInterceptor(
		MethodInvocation methodInvocation,
		MethodInterceptor methodInterceptor) {

		Method method = methodInvocation.getMethod();

		List<MethodInterceptor> methodInterceptors = _methodInterceptors.get(
			method);

		if (methodInterceptors == null) {
			return;
		}

		ArrayList<MethodInterceptor> newMethodInterceptors = new ArrayList<>(
			methodInterceptors);

		newMethodInterceptors.remove(methodInterceptor);

		if (methodInterceptors.equals(_classLevelMethodInterceptors)) {
			newMethodInterceptors = _classLevelMethodInterceptors;
		}
		else {
			newMethodInterceptors.trimToSize();
		}

		_methodInterceptors.put(method, newMethodInterceptors);
	}

	public void reset() {
		_annotations.clear();
		_methodInterceptors.clear();
	}

	private static final Map<Method, Annotation[]> _annotations =
		new ConcurrentHashMap<>();
	private static final Annotation[] _nullAnnotations = new Annotation[0];

	private final
		Map<Class<? extends Annotation>, AnnotationChainableMethodAdvice<?>[]>
			_annotationChainableMethodAdvices = new HashMap<>();
	private final ArrayList<MethodInterceptor> _classLevelMethodInterceptors;
	private final List<MethodInterceptor> _fullMethodInterceptors;
	private final Map<Method, List<MethodInterceptor>> _methodInterceptors =
		new ConcurrentHashMap<>();

}