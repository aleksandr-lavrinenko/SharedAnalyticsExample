#!/usr/bin/env python3

def to_camel(non_camel_case):
    return ''.join(x.capitalize() or '_' for x in non_camel_case.split('_'))