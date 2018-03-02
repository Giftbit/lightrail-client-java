package com.lightrail.feature;


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = ".", glue = "com.lightrail.feature", plugin = {"pretty"})
public class RunCukesTest {
}
