package com.kangong.bootboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kangong.bootboard.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String> {
}
