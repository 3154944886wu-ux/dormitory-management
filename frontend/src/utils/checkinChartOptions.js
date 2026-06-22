/** 归寝统计图表配色与配置 */

export const STATUS_COLORS = {
  normal: '#52C41A',
  late: '#E6A23C',
  absent: '#F56C6C',
  leave: '#909399'
}

export const EXCEPTION_COLORS = {
  1: '#E6A23C',
  '2-0': '#F56C6C',
  '2-1': '#FAB6B6',
  3: '#409EFF'
}

export const EXCEPTION_BAR_SERIES = [
  { key: '1', name: '晚归', color: '#E6A23C', match: row => rowType(row) === 1 },
  { key: '2-0', name: '未归·未处理', color: '#F56C6C', match: row => rowType(row) === 2 && rowHandled(row) !== 1 },
  { key: '2-1', name: '未归·已处理', color: '#FAB6B6', match: row => rowType(row) === 2 && rowHandled(row) === 1 },
  { key: '3', name: '缺卡', color: '#409EFF', match: row => rowType(row) === 3 }
]

function rowType(row) {
  return Number(row.type ?? row.exception_type)
}

function rowHandled(row) {
  return Number(row.handled ?? 0)
}

export const STATUS_LABELS = {
  0: '已归',
  1: '晚归',
  2: '未归',
  3: '请假'
}

export const EXCEPTION_LABELS = {
  1: '晚归',
  2: '未归',
  3: '缺卡'
}

export function shortLabel(name, max = 10) {
  const text = name || '未分组'
  return text.length > max ? `${text.slice(0, max)}…` : text
}

export function formatDateLabel(dateStr) {
  if (!dateStr) return ''
  const parts = String(dateStr).split('-')
  return parts.length >= 3 ? `${parts[1]}/${parts[2]}` : dateStr
}

const baseGrid = { left: 48, right: 24, top: 72, bottom: 48, containLabel: true }

export function buildStatusPieOption(summary = {}) {
  const data = [
    { name: '已归', value: summary.normalCount || 0, itemStyle: { color: STATUS_COLORS.normal } },
    { name: '晚归', value: summary.lateCount || 0, itemStyle: { color: STATUS_COLORS.late } },
    { name: '未归', value: summary.absentCount || 0, itemStyle: { color: STATUS_COLORS.absent } },
    { name: '请假', value: summary.leaveCount || 0, itemStyle: { color: STATUS_COLORS.leave } }
  ].filter(item => item.value > 0)

  return {
    color: Object.values(STATUS_COLORS),
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, type: 'scroll' },
    series: [{
      type: 'pie',
      radius: ['42%', '68%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { formatter: '{b}\n{d}%' },
      data: data.length ? data : [{ name: '暂无数据', value: 1, itemStyle: { color: '#EBEEF5' }, label: { show: false } }]
    }]
  }
}

export function buildExceptionPieOption(summary = {}) {
  const hasSplit = (summary.absentHandledCount ?? 0) + (summary.absentUnhandledCount ?? 0) > 0
  const absentUnhandled = hasSplit ? (summary.absentUnhandledCount ?? 0) : (summary.absentCount ?? 0)
  const absentHandled = hasSplit ? (summary.absentHandledCount ?? 0) : 0
  const data = [
    { name: '晚归', value: summary.lateCount || 0, itemStyle: { color: EXCEPTION_COLORS[1] } },
    { name: '未归·未处理', value: absentUnhandled, itemStyle: { color: EXCEPTION_COLORS['2-0'] } },
    { name: '未归·已处理', value: absentHandled, itemStyle: { color: EXCEPTION_COLORS['2-1'] } },
    { name: '缺卡', value: summary.missingCount || 0, itemStyle: { color: EXCEPTION_COLORS[3] } }
  ].filter(item => item.value > 0)

  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['42%', '68%'],
      center: ['50%', '45%'],
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { formatter: '{b}\n{d}%' },
      data: data.length ? data : [{ name: '暂无数据', value: 1, itemStyle: { color: '#EBEEF5' }, label: { show: false } }]
    }]
  }
}

export function buildDailyTrendOption(dailyTrend = [], dateRange = []) {
  const dates = []
  if (dateRange[0] && dateRange[1]) {
    const start = new Date(dateRange[0])
    const end = new Date(dateRange[1])
    for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
      dates.push(d.toISOString().slice(0, 10))
    }
  } else {
    dates.push(...[...new Set(dailyTrend.map(r => String(r.date || r.check_date || '').slice(0, 10)))].sort())
  }

  const seriesKeys = [
    { status: 0, name: '已归', color: STATUS_COLORS.normal },
    { status: 1, name: '晚归', color: STATUS_COLORS.late },
    { status: 2, name: '未归', color: STATUS_COLORS.absent },
    { status: 3, name: '请假', color: STATUS_COLORS.leave }
  ]

  return {
    tooltip: { trigger: 'axis' },
    legend: { top: 8, type: 'scroll' },
    grid: { ...baseGrid, bottom: 56 },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates.map(formatDateLabel),
      axisLabel: { rotate: dates.length > 10 ? 35 : 0 }
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: seriesKeys.map(({ status, name, color }) => ({
      name,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: { width: 2 },
      itemStyle: { color },
      areaStyle: { color, opacity: 0.08 },
      data: dates.map(date => {
        const row = dailyTrend.find(r => String(r.date || r.check_date || '').startsWith(date) && Number(r.status) === status)
        return row ? Number(row.count) : 0
      })
    }))
  }
}

export function buildGroupedBarOption(title, rows = []) {
  const names = [...new Set(rows.map(r => r.name || '未分组'))]
  const needZoom = names.length > 6
  return {
    ...(title ? { title: { text: title, left: 0, top: 0, textStyle: { fontSize: 14, fontWeight: 600 } } } : {}),
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { top: title ? 28 : 8, type: 'scroll' },
    grid: { ...baseGrid, bottom: needZoom ? 72 : 48 },
    dataZoom: needZoom ? [{ type: 'slider', start: 0, end: Math.min(100, Math.round(600 / names.length)) }] : undefined,
    xAxis: {
      type: 'category',
      data: names.map(n => shortLabel(n, 8)),
      axisLabel: { interval: 0, rotate: names.length > 4 ? 30 : 0 }
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: EXCEPTION_BAR_SERIES.map(({ name, color, match }) => ({
      name,
      type: 'bar',
      stack: 'total',
      barMaxWidth: 36,
      emphasis: { focus: 'series' },
      itemStyle: { color },
      data: names.map(groupName => {
        const matched = rows.filter(r => (r.name || '未分组') === groupName && match(r))
        return matched.reduce((sum, r) => sum + Number(r.count || 0), 0)
      })
    }))
  }
}
