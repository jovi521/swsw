import matplotlib.pyplot as plt
import numpy as np
from mpl_toolkits.basemap import Basemap
import sys
import os
import time
from fy4a import FY4A_AGRI_L1


def create_img(file_path, geo_range, save_dir):
    '''
        file_path:需要解析的文件路径
        geo_range:需要裁剪的区域范围和粒度,格式：最小纬度,最大纬度,最小经度,最大经度,粒度 例如：10, 54, 70, 140, 0.1
        save_path:保存路径
    '''
    # 获得文件名
    filename = file_path.split('\\')[-1]
    # 从文件名中获得时间
    start_time = filename.split('_')[-4]
    # 将世界时转化为北京时
    time_array = time.strptime(start_time, "%Y%m%d%H%M%S")
    time_stamp = int(time.mktime(time_array)) + 8 * 3600
    time_array = time.localtime(time_stamp)
    other_style_time = time.strftime('%Y%m%d%H%M%S', time_array)
    yyyyMMdd = other_style_time[0:8]
    # 卫星类型
    satellite_type = 'FY4A'
    # 通道号
    channel_number = 'Channel12'
    # 读取文件，获得fy4a对象
    fy4a_agri_l1 = FY4A_AGRI_L1(file_path)
    # 选择通道和区域
    fy4a_agri_l1.extract('Channel12', geo_range)
    # 获得通道对象
    channel12 = fy4a_agri_l1.channels['Channel12']
    # 绘图
    # 设置图片大小和dpi
    # plt.subplot(1, 1, 1)
    plt.figure(figsize=(10, 8), dpi=200)
    lat_S, lat_N, lon_W, lon_E, step = eval(geo_range)
    channel12 = np.array(channel12)
    channel12 = np.flip(channel12, axis=0)
    Basemap(projection='merc', llcrnrlat=lat_S, urcrnrlat=lat_N, \
                llcrnrlon=lon_W, urcrnrlon=lon_E, lat_ts=5, resolution='c')
    x = np.arange(lon_W, lon_E + 0.1, 0.1)
    y = np.arange(lat_S, lat_N + 0.1, 0.1)
    xx, yy = np.meshgrid(x, y)
    # 色彩定制：ch012色标
    levels = [198.55, 218.15, 232.38, 243.94, 253.88, 262.69, 270.1, 277.51, 284.39, 290.85,
              296.96, 302.76, 308.32, 313.65, 318.79, 323.74, 328.55, 334.39]
    cdict = (
    '#474447', '#EBA6C3', '#DA5146', '#DCB758', '#C0D9C6', '#99C7E1', '#84ABCF', '#6D8DBC', '#5971A9', '#425396',
    '#303B84', '#242C78', '#1B1F6D', '#1B1F6D', '#111262', '#0a0b5B', '#0a0a5B', '#0c0c5D')
    plt.contourf(xx, yy, channel12, levels=levels, extend='both', colors=cdict)
    # plt.contourf(xx, yy, channel12)
    # 去除边框
    plt.axis('off')
    img_name = '{}{}{}{}{}{}'.format('C012', '_', other_style_time, '_', satellite_type, '.png')
    save_dir = '{}{}{}{}{}{}'.format(save_dir, satellite_type, '/', yyyyMMdd, '/', channel_number.upper())
    if not os.path.exists(save_dir):
        os.makedirs(save_dir)
    save_path = '{}{}{}'.format(save_dir, '/', img_name)
    plt.savefig(save_path, transparent=True, bbox_inches='tight', pad_inches=0)


if __name__ == '__main__':
    file_path = sys.argv[1]
    geo_range = sys.argv[2]
    save_path = sys.argv[3]
    # file_path = 'D:/Z_SATE_C_BAWX_20201217062328_P_FY4A-_AGRI--_N_DISK_1047E_L2-_LSE-_MULT_NOM_20201217060000_20201217061459_012KM_V0001.NC'
    # color_dict = 'D:/color_dict.txt'
    # geo_range = '10,54,70,140,0.1'
    # save_path = 'D:/China.png'
    # file_path = 'D:\\Data\\Z_SATE_C_BAWX_20210131142647_P_FY4A-_AGRI--_N_REGC_1047E_L1-_FDI-_MULT_NOM_20210131141918_20210131142335_4000M_V0001.HDF'
    # geo_range = '10,54,70,140,0.1'
    # save_path = 'D:/Data/satellite_parse/'
    create_img(file_path, geo_range, save_path)
