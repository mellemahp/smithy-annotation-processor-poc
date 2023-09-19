$version: "2.0"

namespace io.smithy.example

@trait(selector: "member")
string jsonName

@trait
structure myComplexTrait {
    fieldA: String
    fieldB: Boolean
}

@trait
integer HttpCode

@trait
list strList {
    member: MyString
}

string MyString
