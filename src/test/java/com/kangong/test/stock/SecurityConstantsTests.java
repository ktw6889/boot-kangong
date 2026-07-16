package com.kangong.test.stock;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.common.security.SecurityConstants;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConstantsTests {

	@Test
	@DisplayName("ROLE_ADMIN 상수값이 정확한지 확인")
	void testRoleAdmin() {
		assertEquals("ROLE_ADMIN", SecurityConstants.ROLE_ADMIN);
	}

	@Test
	@DisplayName("ROLE_MEMBER 상수값이 정확한지 확인")
	void testRoleMember() {
		assertEquals("ROLE_MEMBER", SecurityConstants.ROLE_MEMBER);
	}

	@Test
	@DisplayName("HAS_ROLE_ADMIN이 Spring Security SpEL 형식인지 확인")
	void testHasRoleAdmin() {
		assertEquals("hasRole('ROLE_ADMIN')", SecurityConstants.HAS_ROLE_ADMIN);
	}

	@Test
	@DisplayName("HAS_ROLE_MEMBER가 Spring Security SpEL 형식인지 확인")
	void testHasRoleMember() {
		assertEquals("hasRole('ROLE_MEMBER')", SecurityConstants.HAS_ROLE_MEMBER);
	}

	@Test
	@DisplayName("HAS_ANY_ROLE_ADMIN_MEMBER가 Spring Security SpEL 형식인지 확인")
	void testHasAnyRole() {
		assertEquals("hasAnyRole('ROLE_ADMIN','ROLE_MEMBER')", SecurityConstants.HAS_ANY_ROLE_ADMIN_MEMBER);
	}

	@Test
	@DisplayName("SpEL 표현식에 사용된 역할명이 상수와 일치하는지 확인")
	void testSpelContainsConstants() {
		assertTrue(SecurityConstants.HAS_ROLE_ADMIN.contains(SecurityConstants.ROLE_ADMIN));
		assertTrue(SecurityConstants.HAS_ROLE_MEMBER.contains(SecurityConstants.ROLE_MEMBER));
		assertTrue(SecurityConstants.HAS_ANY_ROLE_ADMIN_MEMBER.contains(SecurityConstants.ROLE_ADMIN));
		assertTrue(SecurityConstants.HAS_ANY_ROLE_ADMIN_MEMBER.contains(SecurityConstants.ROLE_MEMBER));
	}

	@Test
	@DisplayName("모든 상수 필드가 public static final인지 확인")
	void testAllFieldsArePublicStaticFinal() {
		Field[] fields = SecurityConstants.class.getDeclaredFields();
		for (Field field : fields) {
			if (field.isSynthetic()) continue;
			int modifiers = field.getModifiers();
			assertTrue(Modifier.isPublic(modifiers) || Modifier.isPrivate(modifiers),
					field.getName() + " should be public or private");
			assertTrue(Modifier.isStatic(modifiers),
					field.getName() + " should be static");
			assertTrue(Modifier.isFinal(modifiers),
					field.getName() + " should be final");
		}
	}

	@Test
	@DisplayName("SecurityConstants 인스턴스 생성이 불가능한지 확인")
	void testCannotInstantiate() {
		Constructor<?>[] constructors = SecurityConstants.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
	}

	@Test
	@DisplayName("역할명이 ROLE_ 접두사를 가지는지 확인")
	void testRolePrefixConvention() {
		assertTrue(SecurityConstants.ROLE_ADMIN.startsWith("ROLE_"));
		assertTrue(SecurityConstants.ROLE_MEMBER.startsWith("ROLE_"));
	}
}
