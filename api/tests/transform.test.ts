import { transformRequest, TransformationConfig } from '../services/transformService.js';
import assert from 'assert';

console.log('Running Transform Service Tests...');

const runTest = (name: string, fn: () => void) => {
  try {
    fn();
    console.log(`✅ ${name}`);
  } catch (e: any) {
    console.error(`❌ ${name}`);
    console.error(e.message);
    process.exit(1);
  }
};

runTest('Basic Mapping', () => {
  const data = { a: 1 };
  const config: TransformationConfig = {
    mappings: [{ source: 'a', target: 'b' }]
  };
  const result = transformRequest(data, config);
  assert.deepStrictEqual(result, { b: 1 });
});

runTest('Nested Mapping', () => {
  const data = { user: { name: 'John' } };
  const config: TransformationConfig = {
    mappings: [{ source: 'user.name', target: 'customer_name' }]
  };
  const result = transformRequest(data, config);
  assert.strictEqual(result.customer_name, 'John');
  assert.strictEqual(result.user.name, undefined);
  // We accept that result.user might still exist as {}
});

runTest('String Transformation: Uppercase', () => {
  const data = { name: 'john' };
  const config: TransformationConfig = {
    mappings: [{ 
      source: 'name', 
      target: 'name',
      transformations: [{ type: 'uppercase' }]
    }]
  };
  const result = transformRequest(data, config);
  assert.deepStrictEqual(result, { name: 'JOHN' });
});

runTest('Multiple Transformations: Trim + Substring', () => {
  const data = { code: '  123456  ' };
  const config: TransformationConfig = {
    mappings: [{ 
      source: 'code', 
      target: 'code',
      transformations: [
        { type: 'trim' },
        { type: 'substring', params: [0, 3] }
      ]
    }]
  };
  const result = transformRequest(data, config);
  assert.deepStrictEqual(result, { code: '123' });
});

runTest('Conditional Mapping: Exists', () => {
  const data = { a: 1 };
  const config: TransformationConfig = {
    mappings: [
      { 
        source: 'a', 
        target: 'b',
        condition: { operator: 'exists' }
      },
      { 
        source: 'c', 
        target: 'd',
        condition: { operator: 'exists' }
      }
    ]
  };
  const result = transformRequest(data, config);
  assert.deepStrictEqual(result, { b: 1 }); // d should not be created
});

runTest('Default Value', () => {
  const data = {};
  const config: TransformationConfig = {
    mappings: [{ source: 'a', target: 'b', defaultValue: 100 }]
  };
  const result = transformRequest(data, config);
  assert.deepStrictEqual(result, { b: 100 });
});

runTest('Structure Flattening', () => {
  const data = { a: { b: { c: 1 } } };
  const config: TransformationConfig = {
    mappings: [{ source: 'a.b.c', target: 'val' }]
  };
  const result = transformRequest(data, config);
  assert.strictEqual(result.val, 1);
  assert.strictEqual(result.a.b.c, undefined);
});

runTest('Regex Replace', () => {
  const data = { phone: '123-456-7890' };
  const config: TransformationConfig = {
    mappings: [{ 
      source: 'phone', 
      target: 'phone',
      transformations: [{ type: 'replace', params: ['/-/g', ''] }]
    }]
  };
  const result = transformRequest(data, config);
  assert.deepStrictEqual(result, { phone: '1234567890' });
});

console.log('All tests passed!');
