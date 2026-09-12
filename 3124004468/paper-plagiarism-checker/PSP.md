# PSP 表格

> 作业：论文查重  
> 语言：Java  
> 算法：字符级 N-gram + 余弦相似度

## 预估耗时（开发前填写）

| PSP2.1 | Personal Software Process Stages | 预估耗时（分钟） |
| --- | --- | --- |
| **Planning** | **计划** | |
| · Estimate | · 估计这个任务需要多少时间 | 30 |
| **Development** | **开发** | |
| · Analysis | · 需求分析（包括学习新技术） | 60 |
| · Design Spec | · 生成设计文档 | 40 |
| · Design Review | · 设计复审 | 20 |
| · Coding Standard | · 代码规范（为目前的开发制定合适的规范） | 20 |
| · Design | · 具体设计 | 50 |
| · Coding | · 具体编码 | 150 |
| · Code Review | · 代码复审 | 40 |
| · Test | · 测试（自我测试，修改代码，提交修改） | 90 |
| **Reporting** | **报告** | |
| · Test Report | · 测试报告 | 30 |
| · Size Measurement | · 计算工作量 | 15 |
| · Postmortem & Process Improvement Plan | · 事后总结，并提出过程改进计划 | 30 |
| | **合计** | **575** |

## 实际耗时（开发后填写）

| PSP2.1 | Personal Software Process Stages | 实际耗时（分钟） |
| --- | --- | --- |
| **Planning** | **计划** | |
| · Estimate | · 估计这个任务需要多少时间 | 25 |
| **Development** | **开发** | |
| · Analysis | · 需求分析（包括学习新技术） | 50 |
| · Design Spec | · 生成设计文档 | 35 |
| · Design Review | · 设计复审 | 15 |
| · Coding Standard | · 代码规范（为目前的开发制定合适的规范） | 15 |
| · Design | · 具体设计 | 45 |
| · Coding | · 具体编码 | 130 |
| · Code Review | · 代码复审 | 30 |
| · Test | · 测试（自我测试，修改代码，提交修改） | 80 |
| **Reporting** | **报告** | |
| · Test Report | · 测试报告 | 25 |
| · Size Measurement | · 计算工作量 | 10 |
| · Postmortem & Process Improvement Plan | · 事后总结，并提出过程改进计划 | 25 |
| | **合计** | **485** |

## 过程改进总结

1. **需求分析耗时低于预估**：因选用无外部依赖的字符级 N-gram 方案，避免了中文分词库的选型与集成时间。
2. **编码耗时低于预估**：Maven 工程结构与清晰的类划分使编码效率提升；JaCoCo 覆盖率反馈帮助快速定位未覆盖分支。
3. **测试耗时略低于预估**：JUnit 5 的 `@TempDir` 极大简化了文件 I/O 测试的准备工作。
4. **改进计划**：后续可引入 SimHash 用于大规模文档的快速初筛，再对候选对使用 N-gram + 余弦精确计算，兼顾性能与准确度。
