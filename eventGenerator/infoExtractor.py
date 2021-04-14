#!/usr/bin/env python3

class InfoExtractor: 
    # First 5 rows is no event, next 5 events is sending by backend
    # Last row is for comment, we should ignore it
    leftOffset = 11

    def extract_params_names(self, table_content):
        return table_content[2][self.leftOffset:len(table_content[2]) - 1]

    def extract_params_types(self, table_content):
        return table_content[1][self.leftOffset:len(table_content[2]) - 1]

    def extract_events_names(self, table_content):
        return [row[1] for row in table_content[3:]]

    def extract_events_ids(self, table_content):
        return [row[0] for row in table_content[3:]]
        
    def extract_params_for_event(self, table_content):
        params_table = []
        for index in range(3, len(table_content)):
            params_table += [table_content[index][self.leftOffset:len(table_content[2]) - 1]]
        return params_table
