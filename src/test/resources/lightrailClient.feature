Feature: Lightrail Client Core

  @configuration
  Scenario: Lightrail client configuration
    When the [API key] is set to a valid value [sample.apikey.value] it should not throw an error

    When the [API key] is set to an invalid value [] it should throw an error: [LightrailException]

    When the [shared secret] is set to a valid value [sample-secret] it should not throw an error

    When the [shared secret] is set to an invalid value [] it should throw an error: [LightrailException]
