/**
 * 仅解析 JWT payload（不验签），用于路由守卫判断是否过期。
 */
export function decodeJwtPayload(token) {
  if (!token || typeof token !== 'string') {
    return null
  }
  const parts = token.split('.')
  if (parts.length < 2 || !parts[1]) {
    return null
  }
  const normalized = parts[1].replace(/-/g, '+').replace(/_/g, '/')
  const padded = normalized + '='.repeat((4 - (normalized.length % 4)) % 4)
  try {
    return JSON.parse(atob(padded))
  } catch {
    return null
  }
}

export function isTokenExpired(token, nowMs = Date.now()) {
  const payload = decodeJwtPayload(token)
  if (!payload || typeof payload.exp !== 'number') {
    return true
  }
  return payload.exp * 1000 <= nowMs
}
