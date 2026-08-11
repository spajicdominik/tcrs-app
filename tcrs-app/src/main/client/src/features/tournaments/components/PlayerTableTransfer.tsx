import React from 'react';
import { Table, Transfer } from 'antd';
import type { TableColumnsType, TableProps, TransferProps } from 'antd';
import type { PlayerRow } from '../types/PlayerRow.ts';

type TableRowSelection<T extends object> = TableProps<T>['rowSelection'];

const columns: TableColumnsType<PlayerRow> = [
    {
        dataIndex: 'name',
        title: 'Igrač',
    },
];

const filterOption = (input: string, item: PlayerRow) =>
    item.name.toLowerCase().includes(input.toLowerCase());

interface PlayerTableTransferProps {
    /** Every selectable player, already carrying a `key`. */
    players: PlayerRow[];
    /** Keys of the players currently moved to the right-hand list. */
    targetKeys: TransferProps['targetKeys'];
    onChange: TransferProps['onChange'];
}

/** Two-list picker for choosing which players take part in a tournament. */
const PlayerTableTransfer: React.FC<PlayerTableTransferProps> = ({
    players,
    targetKeys,
    onChange,
}) => (
    /*
     * Transfer is two lists plus arrow buttons side by side, which cannot usefully
     * fit on a phone. Rather than restructure it, the pair keeps a readable minimum
     * width and the user swipes across it - the lists themselves stay fully usable.
     */
    <div className="w-full min-w-0 overflow-x-auto">
    <Transfer<PlayerRow>
        style={{ width: '100%', minWidth: 560 }}
        listStyle={{ flex: 1, minWidth: 0 }}
        dataSource={players}
        targetKeys={targetKeys}
        onChange={onChange}
        filterOption={filterOption}
        titles={['Svi igrači', 'U natjecanju']}
        showSearch
        showSelectAll={false}
    >
        {({
              filteredItems,
              onItemSelect,
              onItemSelectAll,
              selectedKeys: listSelectedKeys,
              disabled: listDisabled,
          }) => {
            const rowSelection: TableRowSelection<PlayerRow> = {
                getCheckboxProps: () => ({ disabled: listDisabled }),
                onChange(selectedRowKeys) {
                    onItemSelectAll(selectedRowKeys, 'replace');
                },
                selectedRowKeys: listSelectedKeys,
                selections: [Table.SELECTION_ALL, Table.SELECTION_INVERT, Table.SELECTION_NONE],
            };

            return (
                <Table<PlayerRow>
                    rowSelection={rowSelection}
                    columns={columns}
                    dataSource={filteredItems}
                    size="small"
                    style={{ pointerEvents: listDisabled ? 'none' : undefined }}
                    onRow={({ key }) => ({
                        onClick: () => {
                            if (listDisabled) {
                                return;
                            }
                            onItemSelect(key, !listSelectedKeys.includes(key));
                        },
                    })}
                />
            );
        }}
    </Transfer>
    </div>
);

export default PlayerTableTransfer;
