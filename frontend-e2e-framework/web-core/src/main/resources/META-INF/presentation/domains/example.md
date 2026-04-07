{
"schemaVersion": 1,
"domainId": "web",

"entities": {
"Page": "it.frontend.e2e.framework.web.domain.Page",
"Component": "it.frontend.e2e.framework.web.domain.Component",
"DomNode": "it.frontend.e2e.framework.web.domain.DomNode"
},

"scopes": {
"pages": { "subtypesOf": "it.frontend.e2e.framework.web.domain.Page" },
"components": { "subtypesOf": "it.frontend.e2e.framework.web.domain.Component" },
"domNodes": { "subtypesOf": "it.frontend.e2e.framework.web.domain.DomNode" }
},

"constraints": [
{
"id": "web-page-must-have-pageinfo",
"scope": "pages",
"mustHaveAnnotation": "it.frontend.e2e.framework.web.annotation.PageInfo",
"severity": "ERROR",
"message": "Ogni @Page deve avere @PageInfo"
}
]
}
