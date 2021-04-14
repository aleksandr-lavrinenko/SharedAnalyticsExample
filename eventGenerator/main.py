#!/usr/bin/env python3
from tableLoader import TableLoader
from parametersGenerator import ParamGenerator
from infoExtractor import InfoExtractor
from eventGenerator import EventGenerator

PACKAGE_NAME = 'package com.mobile.analytics.event\n\n'
IMPORT_EVENT = 'import com.mobile.analytics.event.*\n\n'
GENERATED_FOLDER_PATH = '../src/commonMain/kotlin/com/mobile/analytics/generated/'
EVENT_PARAM_FILE_NAME = 'eventParams.generated.kt'
EVENTS_FILE_NAME = 'events.generated.kt'

def main():
    info_extractor = InfoExtractor()
    param_generator = ParamGenerator()
    event_generator = EventGenerator()

    table_content = TableLoader().get_table_content()

    params_types = info_extractor.extract_params_types(table_content)
    params_names = info_extractor.extract_params_names(table_content)
    params_code = param_generator.generate_params_code_list(params_types, params_names)

    with open(GENERATED_FOLDER_PATH + EVENT_PARAM_FILE_NAME, 'w') as f:
        f.writelines([PACKAGE_NAME, IMPORT_EVENT])
        f.writelines(params_code)

    events_names = info_extractor.extract_events_names(table_content)
    events_ids = info_extractor.extract_events_ids(table_content)
    events_params_matrix = info_extractor.extract_params_for_event(table_content)
    events_params_code_matrix = param_generator.generate_param_matrix(events_params_matrix, params_names)
    event_tree = event_generator.transformToMap(events_names)
    events_code = event_generator.generate_events_code(event_tree, events_ids, events_params_code_matrix, "Event", 0)


    with open(GENERATED_FOLDER_PATH + EVENTS_FILE_NAME, 'w') as f:
        f.writelines([PACKAGE_NAME, IMPORT_EVENT])
        f.writelines(events_code)

    
if __name__ == '__main__':
    main()
