import { vi } from 'vitest'
import { config } from '@vue/test-utils'

config.global.stubs = {
  transition: false,
  'transition-group': false
}

const storage = new Map()

global.localStorage = {
  getItem: (key) => (storage.has(key) ? storage.get(key) : null),
  setItem: (key, value) => storage.set(key, String(value)),
  removeItem: (key) => storage.delete(key),
  clear: () => storage.clear()
}

beforeEach(() => {
  storage.clear()
  vi.restoreAllMocks()
})
