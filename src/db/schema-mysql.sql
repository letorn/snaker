create table wf_process (
    id                varchar(32) primary key not null comment '主键id',
    name              varchar(100) comment '流程名称',
    display_name      varchar(200) comment '流程显示名称',
    type              varchar(100) comment '流程类型',
    instance_url      varchar(200) comment '实例url',
    state             tinyint(1) comment '流程是否可用',
    content           longblob comment '流程模型定义',
    version           int(2) comment '版本',
    create_time       varchar(50) comment '创建时间',
    creator           varchar(50) comment '创建人'
)comment='流程定义表';

create table wf_order (
    id                varchar(32) not null primary key comment '主键id',
    parent_id         varchar(32) comment '父流程id',
    process_id        varchar(32) not null comment '流程定义id',
    creator           varchar(50) comment '发起人',
    create_time       varchar(50) not null comment '发起时间',
    expire_time       varchar(50) comment '期望完成时间',
    last_update_time  varchar(50) comment '上次更新时间',
    last_updator      varchar(50) comment '上次更新人',
    priority          tinyint(1) comment '优先级',
    parent_node_name  varchar(100) comment '父流程依赖的节点名称',
    order_no          varchar(50) comment '流程实例编号',
    variable          varchar(2000) comment '附属变量json存储',
    version           int(3) comment '版本'
)comment='流程实例表';

create table wf_task (
    id                varchar(32) not null primary key comment '主键id',
    order_id          varchar(32) not null comment '流程实例id',
    task_name         varchar(100) not null comment '任务名称',
    display_name      varchar(200) not null comment '任务显示名称',
    task_type         tinyint(1) not null comment '任务类型',
    perform_type      tinyint(1) comment '参与类型',
    operator          varchar(50) comment '任务处理人',
    create_time       varchar(50) comment '任务创建时间',
    finish_time       varchar(50) comment '任务完成时间',
    expire_time       varchar(50) comment '任务期望完成时间',
    action_url        varchar(200) comment '任务处理的url',
    parent_task_id    varchar(32) comment '父任务id',
    variable          varchar(2000) comment '附属变量json存储',
    version           tinyint(1) comment '版本'
)comment='任务表';

create table wf_task_actor (
    task_id           varchar(32) not null comment '任务id',
    actor_id          varchar(50) not null comment '参与者id'
)comment='任务参与者表';

create table wf_hist_order (
    id                varchar(32) not null primary key comment '主键id',
    process_id        varchar(32) not null comment '流程定义id',
    order_state       tinyint(1) not null comment '状态',
    creator           varchar(50) comment '发起人',
    create_time       varchar(50) not null comment '发起时间',
    end_time          varchar(50) comment '完成时间',
    expire_time       varchar(50) comment '期望完成时间',
    priority          tinyint(1) comment '优先级',
    parent_id         varchar(32) comment '父流程id',
    order_no          varchar(50) comment '流程实例编号',
    variable          varchar(2000) comment '附属变量json存储'
)comment='历史流程实例表';

create table wf_hist_task (
    id                varchar(32) not null primary key comment '主键id',
    order_id          varchar(32) not null comment '流程实例id',
    task_name         varchar(100) not null comment '任务名称',
    display_name      varchar(200) not null comment '任务显示名称',
    task_type         tinyint(1) not null comment '任务类型',
    perform_type      tinyint(1) comment '参与类型',
    task_state        tinyint(1) not null comment '任务状态',
    operator          varchar(50) comment '任务处理人',
    create_time       varchar(50) not null comment '任务创建时间',
    finish_time       varchar(50) comment '任务完成时间',
    expire_time       varchar(50) comment '任务期望完成时间',
    action_url        varchar(200) comment '任务处理url',
    parent_task_id    varchar(32) comment '父任务id',
    variable          varchar(2000) comment '附属变量json存储'
)comment='历史任务表';

create table wf_hist_task_actor (
    task_id           varchar(32) not null comment '任务id',
    actor_id          varchar(50) not null comment '参与者id'
)comment='历史任务参与者表';

create table wf_surrogate (
    id                varchar(32) primary key not null comment '主键id',
    process_name       varchar(100) comment '流程名称',
    operator          varchar(50) comment '授权人',
    surrogate         varchar(50) comment '代理人',
    odate             varchar(64) comment '操作时间',
    sdate             varchar(64) comment '开始时间',
    edate             varchar(64) comment '结束时间',
    state             tinyint(1) comment '状态'
)comment='委托代理表';
create index idx_surrogate_operator on wf_surrogate (operator);

create table wf_cc_order (
    order_id        varchar(32) comment '流程实例id',
    actor_id        varchar(50) comment '参与者id',
    creator         varchar(50) comment '发起人',
    create_time     varchar(50) comment '抄送时间',
    finish_time     varchar(50) comment '完成时间',
    status          tinyint(1)  comment '状态'
)comment='抄送实例表';
create index idx_ccorder_order on wf_cc_order (order_id);

create index idx_process_name on wf_process (name);
create index idx_order_processid on wf_order (process_id);
create index idx_order_no on wf_order (order_no);
create index idx_task_order on wf_task (order_id);
create index idx_task_taskname on wf_task (task_name);
create index idx_task_parenttask on wf_task (parent_task_id);
create index idx_taskactor_task on wf_task_actor (task_id);
create index idx_hist_order_processid on wf_hist_order (process_id);
create index idx_hist_order_no on wf_hist_order (order_no);
create index idx_hist_task_order on wf_hist_task (order_id);
create index idx_hist_task_taskname on wf_hist_task (task_name);
create index idx_hist_task_parenttask on wf_hist_task (parent_task_id);
create index idx_hist_taskactor_task on wf_hist_task_actor (task_id);

alter table wf_task_actor
  add constraint fk_task_actor_taskid foreign key (task_id)
  references wf_task (id);
alter table wf_task
  add constraint fk_task_orderid foreign key (order_id)
  references wf_order (id);
alter table wf_order
  add constraint fk_order_parentid foreign key (parent_id)
  references wf_order (id);
alter table wf_order
  add constraint fk_order_processid foreign key (process_id)
  references wf_process (id);
alter table wf_hist_task_actor
  add constraint fk_hist_taskactor foreign key (task_id)
  references wf_hist_task (id);
alter table wf_hist_task
  add constraint fk_hist_task_orderid foreign key (order_id)
  references wf_hist_order (id);
alter table wf_hist_order
  add constraint fk_hist_order_parentid foreign key (parent_id)
  references wf_hist_order (id);
alter table wf_hist_order
  add constraint fk_hist_order_processid foreign key (process_id)
  references wf_process (id);