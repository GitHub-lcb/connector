import { Router } from 'express'

const router = Router()

// Echo endpoint - returns everything sent to it
router.all('/echo', (req, res) => {
  res.json({
    message: 'Mock Echo Service',
    received: {
      method: req.method,
      headers: req.headers,
      query: req.query,
      body: req.body
    },
    timestamp: new Date().toISOString()
  })
})

export default router
