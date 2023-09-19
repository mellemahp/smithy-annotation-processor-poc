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
