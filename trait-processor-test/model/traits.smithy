$version: "2.0"

namespace io.smithy.example

/// A JSON name
@trait(selector: "member")
string jsonName

@trait
structure myComplexTrait {
    fieldA: String
    fieldB: Boolean
}

/// An HttpCode
@trait
integer HttpCode

@documentation("A list of strings.")
@trait
list strList {
    member: MyString
}

string MyString

/// A simple enum trait
@trait(selector: "structure > member")
enum myEnum {
    /// Positive response
    YES = "yes"

    /// Negative response
    NO = "no"
}
