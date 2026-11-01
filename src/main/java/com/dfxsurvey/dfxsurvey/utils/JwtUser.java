package com.dfxsurvey.dfxsurvey.utils;

import com.dfxsurvey.dfxsurvey.dao.entity.Role;

// Doğrulanmış bir token'dan okunan kimlik bilgisi
public record JwtUser(String username, Role role) {
}
