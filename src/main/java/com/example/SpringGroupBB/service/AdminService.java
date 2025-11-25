package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
  private final MemberRepository memberRepository;

}
