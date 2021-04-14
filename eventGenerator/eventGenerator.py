#!/usr/bin/env python3

from untils import to_camel

class EventGenerator:
    open = "{"
    close = "}"

    def transformToMap(self, events_names):
        res = {}
        for j, x in enumerate(events_names):
            parts = x.split('__')
            base = res
            lparts = len(parts)
            for i, y in enumerate(parts):
                if i == lparts - 1:
                    base.setdefault(y, j)
                else:
                    base.setdefault(y, {})
                base = base[y]
        return res

    def generate(self, events_names, events_ids, events_params_code_matrix):
        return list(map(self.generateEvent, events_names, events_ids, events_params_code_matrix))
        
    def generateEvent(self, name, id, params):
        components = name.split("__")
        last_class = "Event"
        
        event_code = ""
        namespace_count = len(components) - 1
        for index, component in enumerate(components[:namespace_count]):
            event_code += "".join(["\t"] * index)
            event_code += self.generateNamespace(component, last_class)
            last_class = component

        event_code += self.generateEventClass(components[-1], id, last_class, namespace_count, params)

        event_code += "\n".join([F"{self.close}"] * namespace_count)
        event_code += "\n\n"
        return event_code

    def generateNamespace(self, event_part, last_class, depth):
        offset = "".join(["\t"] * (depth -1))
        return F"{offset}sealed class {to_camel(event_part)}: {to_camel(last_class)}() {self.open}\n"
    
    def generateEventClass(self, event_name, event_id, last_class, depth, params):
        param_names = [name[0].lower() + name[1:] for name in params]
        input_parameters = ", ".join(map(lambda name, param: F"{name}: {param}", param_names, params))
        offset = "".join(["\t"] * (depth -1))

        params_init_string = "override val params = "
        if (len(param_names) > 0):
            params_string = ", ".join(param_names)
            params_init_string += F"arrayOf({params_string}).toMap()"
        else:
            params_init_string += "emptyMap<String, Any>()"
        return  F"""{offset}class {to_camel(event_name)}Event({input_parameters}): {to_camel(last_class)}() {self.open}
    {offset}override val id = id({event_id})
    {offset}{params_init_string}
{offset}{self.close}
"""

    def generate_events_code(self, events_names_tree, events_ids, events_params_code_matrix, last_class, depth):
        event_code = ""
        depth += 1
        for key, value in events_names_tree.items():
            # In case empty row
            if (key == ""):
                continue
            if (type(value) == dict): 
                event_code += self.generateNamespace(key, last_class, depth)
                event_code += self.generate_events_code(value, events_ids, events_params_code_matrix, key, depth)
                event_code += "".join(["\t"] * (depth -1))
                event_code += F"{self.close}"
                event_code += "\n\n"
                continue
            event_id = events_ids[value]
            params = events_params_code_matrix[value]
            event_code += self.generateEventClass(key, event_id, last_class, depth, params)
            event_code += "\n"
        return event_code

    

