package cn.hdj.fastboot.modules.codegen;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import cn.hutool.setting.SettingUtil;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @Author huangjiajian
 */
public class MyBatisPlusGenerator {

    private static Setting setting;

    public static void main(String[] args) {
        setting = SettingUtil.get("generator.setting");
        Setting projectConfig = MyBatisPlusGenerator.setting.getSetting("project-config");

        String includeTables = projectConfig.getStr("project.include.table.names");
        String excludeTables = projectConfig.getStr("project.exclude.table.names");
        String moduleName = projectConfig.getStr("project.module.name");
        String parentModuleName = projectConfig.getStr("project.module.parent");
        String author = projectConfig.getStr("project.author");
        String projectPath = projectConfig.getStr("project.src.path");
        if (StrUtil.isEmpty(projectPath)) {
            projectPath = System.getProperty("user.dir");
        }

        final String outputDir = projectPath;

        String projectXmlPath = projectConfig.getStr("project.xml.path");
        if (StrUtil.isEmpty(projectXmlPath)) {
            projectXmlPath = System.getProperty("user.dir");
        }

        final String mapperXmlOutputDir = projectXmlPath;
        FastAutoGenerator.create(initDataSourceConfig())
                .globalConfig(builder -> {
                    builder.author(author) // 设置作者
                            .commentDate(DatePattern.NORM_DATETIME_PATTERN)
                            .disableOpenDir()
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir(outputDir); // 指定输出目录

                })
                .injectionConfig(builder -> {
                    builder
//                            .customFile()  //自定义模板路径
                            .customMap(
                                    MapUtil.<String, Object>builder()
                                            //开启 lombok 注解
                                            .put("entityLombokModel", true)
                                            .build()
                            );
                })
                .packageConfig(builder -> {
                    builder.parent(parentModuleName) // 设置父包名
                            .moduleName(moduleName) // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, mapperXmlOutputDir)); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    if (StrUtil.isNotEmpty(includeTables)) {
                        builder.addInclude(StrUtil.split(includeTables, ",")); // 设置需要生成的表名
                    } else if (StrUtil.isNotEmpty(excludeTables)) {
                        List<String> split = StrUtil.split(excludeTables, ",");
                        builder.addExclude(split.toArray(new String[0]));
                    }
                })
                .execute();
    }

    /**
     * 读取控制台内容信息
     */
    private static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(("请输入" + tip + "："));
        if (scanner.hasNext()) {
            String next = scanner.next();
            if (StrUtil.isNotEmpty(next)) {
                return next;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    /**
     * 初始化数据源配置
     */
    private static DataSourceConfig.Builder initDataSourceConfig() {
        List<String> groups = setting.getGroups();
        System.out.println("已配置数据源有： ===========> ");
        if (groups == null) {
            throw new MybatisPlusException("请配置数据源");
        }
        List<String> list = groups.stream()
                .filter(group -> StrUtil.startWith(group, "datasource-"))
                .collect(Collectors.toList());
        for (int i = 0; i < list.size(); i++) {
            System.out.println(String.format("%d: %s", i, list.get(i)));
        }
        String index;
        boolean flag = false;
        do {
            try {
                index = scanner("数据源编号");
                if (NumberUtil.isNumber(index)) {
                    long value = NumberUtil.parseNumber(index).longValue();
                    if (value >= 0 && value < list.size()) {
                        flag = true;
                    }
                }
            } catch (Exception e) {
                System.out.println("数据源编码错误：");
                index = scanner("数据源编号");
            }
        } while (!flag);
        Setting setting = MyBatisPlusGenerator.setting.getSetting(list.get(NumberUtil.parseNumber(index).intValue()));

        return new DataSourceConfig.Builder(
                setting.getStr("dataSource.url"),
                setting.getStr("dataSource.username"),
                setting.getStr("dataSource.password")
        );

    }
}
