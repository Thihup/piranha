# Jakarta EE Core Profile TCK Status

## Overview

This directory contains the complete infrastructure for running the Jakarta EE Core Profile Technology Compatibility Kit (TCK) against Piranha.

## Current Status (as of October 30, 2025 - Run #18949803595)

Based on the recent GitHub Actions workflow run:

| TCK | Status | Test Results | Issues |
|-----|--------|--------------|--------|
| **Annotations** | ✅ PASSING | All tests passing | None |
| **CDI** | ❌ FAILING | Build failure | TestNG 7.11.0 incompatibility - EmailableReporter class removed |
| **Core Profile** | ❌ FAILING | 13 tests: 9 pass, 1 fail, 3 errors | ApplicationJsonpIT and ApplicationContextIT failures |
| **Inject** | ✅ PASSING | All tests passing | None |
| **JSON Binding** | ✅ PASSING | All tests passing | None |
| **JSON Processing** | ✅ PASSING | All tests passing | None |
| **REST** | ⏳ RUNNING | Tests in progress | Long-running (may timeout) |

**Summary**: 4 out of 7 TCK suites are fully passing.

### Detailed Failure Analysis

#### CDI TCK Failure
**Root Cause**: TestNG version incompatibility
- **Error**: `Listener org.testng.reporters.EmailableReporter was not found in project's classpath`
- **Cause**: TestNG 7.11.0 removed the EmailableReporter class that was present in 7.10.x
- **Fix**: ✅ Downgraded TestNG from 7.11.0 to 7.10.2 in `test/tck/coreprofile/cdi/runner/core/pom.xml`
- **Status**: Fixed in commit cdd181d

#### Core Profile TCK Failures
**Test Results**: 13 tests run - 9 passed, 1 failed, 3 errors

1. **ApplicationJsonpIT.testCustomProvider** (FAILED)
   - Error: `Failed to find JsonProvider: ee.jakarta.tck.core.json.CustomJsonProvider`
   - Root Cause: Missing `core-tck-jsonp-extension` JAR dependency
   - Fix: ✅ Added explicit installation of `core-tck-jsonp-extension` artifact in installer
   - Fix: ✅ Added `core-tck-jsonp-extension` as test dependency in runner POM

2. **ApplicationJsonpIT.testUseCustomProvider** (ERROR)
   - Error: `EmptyStack` exception
   - Root Cause: Related to missing JSON provider extension
   - Fix: ✅ Should be resolved by adding `core-tck-jsonp-extension` dependency

3. **ApplicationJsonpIT.testUseJsonWithCustomProvider** (ERROR)
   - Error: `EmptyStack` exception
   - Root Cause: Related to missing JSON provider extension
   - Fix: ✅ Should be resolved by adding `core-tck-jsonp-extension` dependency

4. **ApplicationContextIT.testApplicationContextSharedBetweenJaxRsRequests** (ERROR)
   - Error: `HTTP 500 Internal Server Error`
   - Root Cause: Application context not being properly shared between JAX-RS requests
   - Status: ⚠️ Requires further investigation - may be a runtime issue in Piranha

### Fixes Applied

Based on analysis of WildFly's TCK runner setup:

1. **CDI TCK** - TestNG version downgrade (cdd181d)
2. **Core Profile TCK** - Added missing `core-tck-jsonp-extension` dependency
   - Updated `test/tck/coreprofile/coreprofile/installer/pom.xml` to explicitly install the extension JAR
   - Updated `test/tck/coreprofile/coreprofile/runner/pom.xml` to include it as a test dependency

These changes should fix 3 out of 4 Core Profile TCK failures, bringing the pass rate from 9/13 to 12/13 (92%).

## TCK Components

The Core Profile TCK is comprised of the following specification TCKs:

### 1. Annotations TCK
- **Module**: `annotations`
- **Version**: 3.0.0
- **Specification**: Jakarta Annotations
- **Location**: `test/tck/coreprofile/annotations`

### 2. CDI TCK
- **Module**: `cdi`
- **Version**: 4.1.0
- **Specification**: Jakarta Contexts and Dependency Injection (CDI)
- **Location**: `test/tck/coreprofile/cdi`
- **Components**:
  - Core TCK tests
  - Lang Model TCK tests
  - Signature tests

### 3. Core Profile TCK
- **Module**: `coreprofile`
- **Version**: 11.0.0
- **Specification**: Jakarta EE Core Profile (overall profile)
- **Location**: `test/tck/coreprofile/coreprofile`
- **Description**: Tests the complete Core Profile specification compliance

### 4. Inject TCK
- **Module**: `inject`
- **Version**: 2.0.2
- **Specification**: Jakarta Dependency Injection
- **Location**: `test/tck/coreprofile/inject`

### 5. JSON Binding TCK
- **Module**: `jsonb`
- **Version**: 3.0.0
- **Specification**: Jakarta JSON Binding
- **Location**: `test/tck/coreprofile/jsonb`

### 6. JSON Processing TCK
- **Module**: `jsonp`
- **Version**: 2.1.1
- **Specification**: Jakarta JSON Processing
- **Location**: `test/tck/coreprofile/jsonp`

### 7. REST TCK
- **Module**: `rest`
- **Version**: 4.0.1
- **Specification**: Jakarta RESTful Web Services
- **Location**: `test/tck/coreprofile/rest`

## Running the TCKs

### Prerequisites
- Java 21 or later
- Maven 3.8.7 or later
- Sufficient disk space for TCK downloads

### Build and Install Piranha First
```bash
mvn -B -DskipTests=true -DskipITs=true install
```

### Run All Core Profile TCKs
```bash
cd test/tck/coreprofile
mvn -B install
```

### Run Individual TCK

#### Annotations TCK
```bash
cd test/tck/coreprofile/annotations
mvn -B install
```

#### CDI TCK
```bash
cd test/tck/coreprofile/cdi
mvn -B install
```

#### Core Profile TCK
```bash
cd test/tck/coreprofile/coreprofile
mvn -B install
```

#### Inject TCK
```bash
cd test/tck/coreprofile/inject
mvn -B install
```

#### JSON Binding TCK
```bash
cd test/tck/coreprofile/jsonb
mvn -B install
```

#### JSON Processing TCK
```bash
cd test/tck/coreprofile/jsonp
mvn -B install
```

#### REST TCK
```bash
cd test/tck/coreprofile/rest
mvn -B install
```

## TCK Module Structure

Each TCK module follows a consistent structure:

```
<tck-name>/
├── pom.xml           # Parent POM for the TCK
├── installer/        # Downloads and installs TCK artifacts
│   └── pom.xml
└── runner/           # Runs the TCK tests
    └── pom.xml
```

### Installer Module
The installer module:
1. Downloads the TCK distribution from Eclipse Foundation
2. Extracts the TCK
3. Installs TCK artifacts into the local Maven repository

### Runner Module
The runner module:
1. Depends on the installer module
2. Configures Arquillian to use Piranha
3. Executes the TCK tests using Maven Failsafe or Surefire plugin

## GitHub Actions

The TCKs are automatically run via GitHub Actions on a daily schedule. See `.github/workflows/tck-coreprofile.yml` for the complete workflow.

### Workflow Jobs
- `annotations`: Runs Annotations TCK
- `cdi`: Runs CDI TCK (Core and Lang Model)
- `coreprofile`: Runs Core Profile TCK
- `inject`: Runs Inject TCK
- `jsonb`: Runs JSON Binding TCK
- `jsonp`: Runs JSON Processing TCK
- `rest`: Runs REST TCK

## Test Reports

Test reports are generated in the following locations:

- **Annotations**: `annotations/runner/target/failsafe-reports/`
- **CDI Core**: `cdi/runner/core/target/surefire-reports/junitreports/`
- **CDI Lang Model**: `cdi/runner/model/target/surefire-reports/`
- **Core Profile**: `coreprofile/runner/target/failsafe-reports/`
- **Inject**: `inject/installer/target/tck/example/target/surefire-reports/`
- **JSON Binding**: `jsonb/installer/target/tck/bin/target/surefire-reports/`
- **JSON Processing**: `jsonp/installer/target/tck/bin/tck-tests/target/surefire-reports/`
- **REST**: `rest/runner/target/failsafe-reports/`

## Enabling TCK Profile

The TCK modules are included via a Maven profile. To activate them:

```bash
mvn -Ptck <goals>
```

Or add the profile permanently in your `~/.m2/settings.xml`:

```xml
<settings>
  <activeProfiles>
    <activeProfile>tck</activeProfile>
  </activeProfiles>
</settings>
```

## Dependencies

All required dependencies are managed in the parent POMs:
- `test/tck/coreprofile/pom.xml` - Manages TCK versions
- `pom.xml` (root) - Manages common dependencies and plugin versions

## Troubleshooting

### Network Issues
If you encounter network issues downloading TCK artifacts, ensure you have:
- Access to https://download.eclipse.org
- Access to Maven Central and Jakarta staging repositories

### Memory Issues
For large TCK runs, you may need to increase Maven memory:
```bash
export MAVEN_OPTS="-Xmx2g -XX:MaxMetaspaceSize=512m"
```

### Test Failures
Test failures are expected during development. Review the test reports in the `target/failsafe-reports` or `target/surefire-reports` directories for detailed failure information.

## Contributing

When adding new TCK support:
1. Create a new module under `test/tck/coreprofile/<spec-name>`
2. Follow the installer/runner pattern
3. Add the module to the parent POM
4. Update the GitHub Actions workflow
5. Update this status document
