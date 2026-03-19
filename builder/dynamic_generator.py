import json
import os

input_file = 'application_layout.json'
output_file = 'chain-config.json'

with open(input_file, 'r', encoding='utf-8') as f:
    layout_data = json.load(f)

chain_config = []

for partition in layout_data["partitions"]:
    for function in partition["functions"]:
        step = {
            "name": function["name"],
            "isParallel": function["is_parallel"]
        }
        chain_config.append(step)

with open(output_file, 'w', encoding='utf-8') as f:
    json.dump(chain_config, f, indent=2)