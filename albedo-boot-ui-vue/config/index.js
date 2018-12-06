

// see http://vuejs-templates.github.io/webpack for documentation.
var path = require('path')
// var baseUrl = 'http://192.168.6.110:8061';
var baseUrl = 'http://192.168.6.21:8071';

module.exports = {
  build: {
    env: require('./prod.env'),
    index: path.resolve(__dirname, '../dist/index.html'),
    assetsRoot: path.resolve(__dirname, '../dist'),
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',
    productionSourceMap: false,
    // Gzip off by default as many popular static hosts such as
    // Surge or Netlify already gzip all static assets for you.
    // Before setting to `true`, make sure to:
    // npm install --save-dev compression-webpack-plugin
    productionGzip: false,
    productionGzipExtensions: ['js', 'css'],
    // Run the build command with an extra argument to
    // View the bundle analyzer report after build finishes:
    // `npm run build --report`
    // Set to `true` or `false` to always turn it on or off
    bundleAnalyzerReport: process.env.npm_config_report
  },
  dev: {
    env: require('./dev.env'),
    port: 8000,
    autoOpenBrowser: true,
    assetsSubDirectory: 'static',
    host: 'localhost',
    assetsPublicPath: '/',
    proxyTable: {
      '/management': {
        target: baseUrl,
        changeOrigin: true,
        pathRewrite: {
          '^/management': '/management'
        }
      },
      '/api': {
        target: baseUrl,
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      }
    },
    cssSourceMap: false
  }
}
