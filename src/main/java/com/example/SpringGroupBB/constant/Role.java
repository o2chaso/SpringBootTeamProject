package com.example.SpringGroupBB.constant;

public enum Role {
  ADMIN("관리자"),
  USER("정회원");

  private final String korean;

  Role(String korean) {
    this.korean = korean;
  }

  public String getKorean() {
    return korean;
  }
}
