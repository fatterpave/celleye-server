**Config for Infrastructure Manager**

db:
  url: jdbc:sqlserver://noh-vsql20sql:1433;databaseName=IntelinetTMSTest
  user: IntelinetTMS
  pass: Ts2013

server:
    applicationContextPath: /*
    applicationConnectors:
        - keyStorePassword: intelecom
          keyStorePath: keystore.jks
          type: https
          port: 9443
          validateCerts: false

logging:
  level: INFO
  loggers:
    "no.intelecom.responsecenter": 
      level: INFO
      additive: false
      appenders:
        - type: file
          currentLogFilename: responsecenter_app-server.log
          archivedLogFilenamePattern: responsecenter_app-server-%d.log.gz
          archivedFileCount: 15
          timeZone: Europe/Brussels
  appenders:
    - type: console