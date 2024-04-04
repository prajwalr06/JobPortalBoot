package com.s13sh.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.s13sh.jobportal.dto.PortalUser;

public interface PortalUserRepository extends JpaRepository<PortalUser,Integer>
{

	boolean existsByEmailAndVerifiedTrue(String email);

	PortalUser findByEmail(String email);

	PortalUser findByMobile(long mobile);

}
