/**
 * Created by bhou on 6/10/14.
 */

module.exports = {
  mongostore: {
    db: 'certifoto',
    host: 'localhost',
    port: '27017',
    username: '',
    password: ''
  },
  app: "53aa8fd92469165830206986",
  token: "53aa8fd92469165830206987",
  idpUrl: "http://localhost:3001",

  samlOptions: {
    path: '/saml/callback',
    entryPoint: 'http://localhost:3001/samlp',
    issuer: 'certifoto',
    protocol: 'https://',

    cert: 'MIICrzCCAhgCCQDEfKYKtNLU+zANBgkqhkiG9w0BAQUFADCBmzELMAkGA1UEBhMCRlIxFzAVBgNVBAgMDkFscGVzTWFyaXRpbWVzMREwDwYDVQQHDAhWQUxCT05ORTESMBAGA1UECgwJQ0VSVElGT1RPMRIwEAYDVQQLDAlDRVJUSUZPVE8xFTATBgNVBAMMDDExMi4xMjYuNjguMjEhMB8GCSqGSIb3DQEJARYSYm8uaG91QG91dGxvb2suY29tMB4XDTE0MDYxOTA4MzMwOFoXDTE1MDYxOTA4MzMwOFowgZsxCzAJBgNVBAYTAkZSMRcwFQYDVQQIDA5BbHBlc01hcml0aW1lczERMA8GA1UEBwwIVkFMQk9OTkUxEjAQBgNVBAoMCUNFUlRJRk9UTzESMBAGA1UECwwJQ0VSVElGT1RPMRUwEwYDVQQDDAwxMTIuMTI2LjY4LjIxITAfBgkqhkiG9w0BCQEWEmJvLmhvdUBvdXRsb29rLmNvbTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAswmNvuTGoQMqDYVUTlFyU56PRuOL3MHrOMpa1v6eL/5GsoQV3KaJeRyJvrO4R2OeTBPMOeQmX7+qoeNcUJc4qqrQc9wQP+1QgNusZ353oEGBUpLotn6b8o5Wn147pWndnh/qhOg9UGTwrgAU1IBtuPVfTgdpqb+b/XyQczg+t/ECAwEAATANBgkqhkiG9w0BAQUFAAOBgQA0NjXZAcsPHtxOqaW99/rV4LcUeoQBfKI9NKJ3ff9ldk23Cp+SPevgQady4p18C+XRN4p54cG5/X9PJck/xjwT5rDGIWcxeacyysbYdHbdysuOte1gFivCx6wtXghMsTy5Ej9MY2Y3H/TrVm1JRomei9svkQ1R2RZrF+WhPhdaSQ=='
  }
}