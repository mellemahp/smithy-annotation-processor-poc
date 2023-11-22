
rootProject.name = "smithy-annotation-processor"
include("processor", ":trait-processor:annotation", ":trait-processor:processor")
include("trait-processor-test")
include("trait-codegen", "trait-codegen-test")
include("trait-processor-test-test")