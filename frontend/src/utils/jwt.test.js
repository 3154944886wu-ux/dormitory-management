import assert from 'node:assert/strict'
import { test } from 'node:test'
import { isTokenExpired } from './jwt.js'

function tokenWithPayload(payload) {
  const json = JSON.stringify(payload)
  const payloadPart = Buffer.from(json).toString('base64url')
  return `eyJhbGciOiJIUzM4NCJ9.${payloadPart}.sig`
}

test('unpadded JWT payload is not treated as expired when exp is in the future', () => {
  const token = tokenWithPayload({
    role: 'STUDENT',
    id: 4,
    username: '20230001',
    sub: '20230001',
    iat: 1787834435,
    exp: 1788439235
  })
  assert.equal(token.split('.')[1].length % 4, 3)
  assert.equal(isTokenExpired(token, 1787834435 * 1000), false)
})

test('expired exp is detected', () => {
  const token = tokenWithPayload({ exp: 100 })
  assert.equal(isTokenExpired(token, 200000), true)
})

test('invalid or missing token is treated as expired', () => {
  assert.equal(isTokenExpired('not-a-jwt'), true)
  assert.equal(isTokenExpired(''), true)
  assert.equal(isTokenExpired(null), true)
  assert.equal(isTokenExpired(tokenWithPayload({ sub: 'x' })), true)
})
