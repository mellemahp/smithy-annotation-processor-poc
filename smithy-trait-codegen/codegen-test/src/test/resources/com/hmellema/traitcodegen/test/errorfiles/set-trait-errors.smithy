$version: "2.0"

namespace test.smithy.traitcodegen

// Doesnt have unique items. Expect failure
@NumberSetTrait([1,1,3,4])
structure repeatedNumberValues {
}