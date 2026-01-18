import _ from 'lodash'

export type TransformationType = 
  | 'uppercase' 
  | 'lowercase' 
  | 'trim'
  | 'substring' 
  | 'concat' 
  | 'replace' 
  | 'split' 
  | 'join'
  | 'base64_encode' 
  | 'base64_decode'
  | 'number'
  | 'string'
  | 'boolean'
  | 'json_parse'
  | 'json_stringify'

export interface TransformationStep {
  type: TransformationType
  params?: any[] // e.g., substring: [0, 5], replace: ["find", "replace"]
}

export interface ConditionRule {
  operator: 'exists' | 'not_exists' | 'equals' | 'not_equals' | 'contains' | 'gt' | 'lt'
  value?: any
}

export interface MappingRule {
  source: string
  target: string
  defaultValue?: any
  transformations?: TransformationStep[]
  condition?: ConditionRule
}

export interface TransformationConfig {
  mappings?: MappingRule[]
  keepUnmapped?: boolean
}

const applyTransformation = (value: any, step: TransformationStep): any => {
  if (value === undefined || value === null) return value

  switch (step.type) {
    case 'uppercase':
      return String(value).toUpperCase()
    case 'lowercase':
      return String(value).toLowerCase()
    case 'trim':
      return String(value).trim()
    case 'substring':
      const [start, end] = step.params || []
      return String(value).substring(start, end)
    case 'concat':
      const [suffix] = step.params || []
      return String(value) + suffix
    case 'replace':
      const [pattern, replacement] = step.params || []
      // Support simple regex if pattern starts with / and ends with /flags
      if (typeof pattern === 'string' && pattern.startsWith('/') && pattern.lastIndexOf('/') > 0) {
        const lastSlash = pattern.lastIndexOf('/')
        const regexBody = pattern.substring(1, lastSlash)
        const flags = pattern.substring(lastSlash + 1)
        try {
          return String(value).replace(new RegExp(regexBody, flags), replacement || '')
        } catch (e) {
          return String(value).replace(pattern, replacement || '')
        }
      }
      return String(value).replace(pattern, replacement || '')
    case 'split':
      const [separator] = step.params || []
      return String(value).split(separator)
    case 'join':
      const [joiner] = step.params || []
      return Array.isArray(value) ? value.join(joiner) : value
    case 'base64_encode':
      return Buffer.from(String(value)).toString('base64')
    case 'base64_decode':
      return Buffer.from(String(value), 'base64').toString('utf-8')
    case 'number':
      return Number(value)
    case 'string':
      return String(value)
    case 'boolean':
      return Boolean(value)
    case 'json_parse':
      try { return JSON.parse(value) } catch { return value }
    case 'json_stringify':
      return JSON.stringify(value)
    default:
      return value
  }
}

const checkCondition = (value: any, condition?: ConditionRule): boolean => {
  if (!condition) return true

  switch (condition.operator) {
    case 'exists': return value !== undefined && value !== null
    case 'not_exists': return value === undefined || value === null
    case 'equals': return _.isEqual(value, condition.value)
    case 'not_equals': return !_.isEqual(value, condition.value)
    case 'contains': return String(value).includes(condition.value)
    case 'gt': return Number(value) > Number(condition.value)
    case 'lt': return Number(value) < Number(condition.value)
    default: return true
  }
}

/**
 * Transforms the input data based on the provided configuration.
 */
export const transformRequest = (data: any, config: TransformationConfig | null): any => {
  if (!config || !config.mappings || config.mappings.length === 0) {
    return data
  }

  if (typeof data !== 'object' || data === null) {
    return data
  }

  // Deep clone to start
  const result = _.cloneDeep(data)

  config.mappings.forEach(rule => {
    let value = _.get(data, rule.source)

    // 1. Condition Check
    if (rule.condition) {
      if (!checkCondition(value, rule.condition)) {
        return // Skip this rule if condition fails
      }
    }

    // 2. Default Value
    if ((value === undefined || value === null) && rule.defaultValue !== undefined) {
      value = rule.defaultValue
    }

    // 3. Transformations
    if (value !== undefined && value !== null && rule.transformations) {
      rule.transformations.forEach(step => {
        value = applyTransformation(value, step)
      })
    }

    // 4. Set Target
    if (value !== undefined) {
      _.set(result, rule.target, value)

      // 5. Cleanup Source (if moved)
      if (rule.source !== rule.target) {
        // Only unset if we haven't already set something else there (edge case)
        // Simple logic: unset source. 
        _.unset(result, rule.source)
      }
    }
  })

  return result
}
