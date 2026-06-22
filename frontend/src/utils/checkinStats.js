/** 从接口数据或原始记录聚合归寝/异常统计 */

function rowCount(row) {
  return Number(row.count || 0)
}

function rowType(row) {
  return Number(row.type ?? row.exception_type)
}

function rowHandled(row) {
  return Number(row.handled ?? 0)
}

export function summarizeExceptionRows(rows = []) {
  let lateCount = 0
  let absentCount = 0
  let missingCount = 0
  let absentHandledCount = 0
  let absentUnhandledCount = 0

  for (const row of rows) {
    const type = rowType(row)
    const count = rowCount(row)
    const handled = rowHandled(row)
    const hasHandled = Object.prototype.hasOwnProperty.call(row, 'handled')

    if (type === 1) lateCount += count
    else if (type === 2) {
      absentCount += count
      if (hasHandled) {
        if (handled === 1) absentHandledCount += count
        else absentUnhandledCount += count
      }
    } else if (type === 3) missingCount += count
  }

  if (absentCount > 0 && absentHandledCount === 0 && absentUnhandledCount === 0) {
    absentUnhandledCount = absentCount
  }

  return {
    lateCount,
    absentCount,
    absentHandledCount,
    absentUnhandledCount,
    missingCount,
    totalCount: lateCount + absentCount + missingCount,
    unhandledCount: 0
  }
}

export function summarizeExceptionList(items = []) {
  const rows = items.map(item => ({
    type: item.exceptionType ?? item.exception_type,
    handled: item.handled ?? 0,
    count: 1
  }))
  const summary = summarizeExceptionRows(rows)
  summary.unhandledCount = items.filter(item => Number(item.handled) !== 1).length
  return summary
}

export function aggregateExceptionTrend(items = [], groupKey = 'buildingName') {
  const map = new Map()
  for (const item of items) {
    const name = item[groupKey] || item.className || item.building_name || '未分组'
    const type = Number(item.exceptionType ?? item.exception_type)
    const handled = Number(item.handled ?? 0)
    const key = `${name}:${type}:${handled}`
    map.set(key, (map.get(key) || 0) + 1)
  }
  return [...map.entries()].map(([key, count]) => {
    const [name, type, handled] = key.split(':')
    return { name, type: Number(type), handled: Number(handled), count }
  })
}

export function aggregateCheckInRecords(records = []) {
  const summary = { normalCount: 0, lateCount: 0, absentCount: 0, leaveCount: 0, totalCount: 0 }
  const dailyMap = new Map()

  for (const record of records) {
    const status = Number(record.status)
    if (status === 0) summary.normalCount += 1
    else if (status === 1) summary.lateCount += 1
    else if (status === 2) summary.absentCount += 1
    else if (status === 3) summary.leaveCount += 1

    const date = String(record.checkDate || record.check_date || '').slice(0, 10)
    if (!date) continue
    const key = `${date}:${status}`
    dailyMap.set(key, (dailyMap.get(key) || 0) + 1)
  }

  summary.totalCount = summary.normalCount + summary.lateCount + summary.absentCount + summary.leaveCount

  const dailyTrend = []
  for (const [key, count] of dailyMap.entries()) {
    const [date, status] = key.split(':')
    dailyTrend.push({ date, status: Number(status), count })
  }
  dailyTrend.sort((a, b) => a.date.localeCompare(b.date) || a.status - b.status)

  return { summary, dailyTrend }
}

export function mergeExceptionSummary(apiSummary, rows = []) {
  const computed = summarizeExceptionRows(rows)
  if (!apiSummary || apiSummary.totalCount == null) {
    return computed
  }
  return {
    lateCount: apiSummary.lateCount ?? computed.lateCount,
    absentCount: apiSummary.absentCount ?? computed.absentCount,
    absentHandledCount: apiSummary.absentHandledCount ?? computed.absentHandledCount,
    absentUnhandledCount: apiSummary.absentUnhandledCount ?? computed.absentUnhandledCount,
    missingCount: apiSummary.missingCount ?? computed.missingCount,
    totalCount: apiSummary.totalCount ?? computed.totalCount,
    unhandledCount: apiSummary.unhandledCount ?? computed.unhandledCount
  }
}
