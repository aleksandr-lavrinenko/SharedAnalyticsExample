#!/usr/bin/env python3

from untils import to_camel

class ParamGenerator:     
    def create_param(self, name, type):
        name = to_camel(name.strip())
        type = type.strip().capitalize()
        if type == "Int":
            type = "Long"
        return F"data class {name}Param(override val value: {type}) : {type}Param(value)\n"

    def generate_params_code_list(self, params_types, params_names):
        return list(map(self.create_param, params_names, params_types))

    def generate_param_matrix(self, event_params_matrix, params_names):
        params_matrix = []
        for params in event_params_matrix:
            params_code = []
            for index, param in enumerate(params):
                if (param == "x"):
                    class_name = params_names[index]
                    params_code.append(F"{to_camel(class_name)}Param")
            params_matrix.append(params_code)
        return params_matrix


