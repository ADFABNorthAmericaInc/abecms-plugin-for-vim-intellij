function! GetType(slug)
	if a:slug == "t"
	   let type = "text"
	elseif a:slug == "i"
	   let type = "image" 
	elseif a:slug == "tt"
	   let type = "textarea" 
	elseif a:slug == "I"
	   let type = "import" 
	elseif a:slug == "f"
	   let type = "file" 
	elseif a:slug == "l"
	   let type = "link" 
	elseif a:slug == "d"
	   let type = "data" 
	elseif a:slug == "s"
	   let type = "slug" 
	else
		let type = "text"
	endif
	return type
endfun

function! CreateDesc(key)
	let desc = split(a:key, "_")
	let desc = join(desc[1:], " ")
	return toupper(desc[0]).desc[1:]
endfun

function! CreateTab(key)
	let tab = split(a:key, "_")
	if len(tab) < 1
		return "default"
	else
		return toupper(tab[0][0]).tab[0][1:]
	endif
endfun

"type, key, desc, tab or type, file
function! CreateTag(type, ...)
   if a:0 < 1
		let arg2 = ''
		let arg3 = ''
		let arg4 = 'default'
	else
		let arg2 = a:1
		let arg3 = a:2
		let arg4 = a:3
	endif
	if a:type == "import"
		return ["{{abe type='" . a:type . "' file='" . arg2 . ".html'}}"]
	else
		return ["{{abe type='" . a:type . "' key='" . arg2 . "' desc='" . arg3 . "' tab='".arg4."'}}"]
	endif
endfun

function! GetPropositions(word)
	let parts = split(a:word, ':')
	let type = GetType(parts[0])
	if len(parts) > 1
		let key = parts[1]
		let desc = CreateDesc(key)
		let tab = CreateTab(key)
		return CreateTag(type, key, desc, tab)
	else
		return CreateTag(type)
	endif
endfunc

function! abecomplete#Complete(findstart, base)
    if a:findstart
        " locate the start of the word
        let line = getline('.')
        let start = col('.') - 1
        while start > 0 && (line[start - 1] =~ '\a' || line[start - 1] =~ '.' || line[start - 1] =~ '-')
            let start -= 1
        endwhile
        return start
    else
        " find classes matching "a:base"
			return GetPropositions(a:base)	 	  
    endif
endfun
