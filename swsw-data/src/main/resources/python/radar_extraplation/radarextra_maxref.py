import netCDF4 as nc
from mpl_toolkits.basemap import Basemap
import matplotlib.pyplot as plt
import os
import numpy as np
import time
import datetime
import sys

def resolve_data(filepath, savedir):
    # filepath = r'D:/Data/20180516_210000.opflow.nc'
    # savedir = r'D:/Data/weatherRadarExtrapolation_parse/'
    # 获得源文件的文件名
    orginal_filename = filepath.split('\\')[-1]
    # 获得文件的日期
    time_str = orginal_filename[0 : 15]
    timeArray = time.strptime(time_str, '%Y%m%d_%H%M%S')
    # timeArray可以调用tm_year等
    timeStamp = int(time.mktime(timeArray))
    timeStamp += 8 * 3600
    dateArray = datetime.datetime.fromtimestamp(timeStamp)
    yyyyMMddHHmmss = dateArray.strftime('%Y%m%d%H%M%S')
    yyyyMMdd = yyyyMMddHHmmss[0:8]
    HHmmss = yyyyMMddHHmmss[8:14]
    # 获得文件的雷达
    create_radar = orginal_filename.split('.')[1]
    if create_radar == 'SA':
        create_radar = 'Z9280'
    elif create_radar == 'XPAR':
        create_radar = 'Multistation'
    # 获得文件的方法
    create_method = orginal_filename.split('.')[2]
    # 产品类型
    product_type = 'MAXREF'
    dataset = nc.Dataset(filepath, 'r')
    orginal_lon = dataset.variables['lon'][:]
    orginal_lat = dataset.variables['lat'][:]
    cr = dataset.variables['cr'][:]
    lon_min = 101
    lon_max = 106
    lat_min = 28
    lat_max = 33
    # 对原始经纬度进行截取
    lon_idx = np.where((orginal_lon >= float(lon_min)) & (orginal_lon <= float(lon_max)))
    after_lon = orginal_lon[lon_idx]
    lat_idx = np.where((orginal_lat >= float(lat_min)) & (orginal_lat <= float(lat_max)))
    after_lat = orginal_lat[lat_idx]
    lon_min_ind = lon_idx[0][0]
    lon_max_ind = lon_idx[0][-1]
    lat_min_ind = lat_idx[0][0]
    lat_max_ind = lat_idx[0][-1]
    timestep = dataset.variables['timestep'][:]
    cr_after = cr[0:len(timestep) + 1, lat_min_ind:lat_max_ind + 1, lon_min_ind: lon_max_ind + 1]

    # 获取cr数组的维度
    cr_d_count = cr.ndim
    # 获取cr数组的每个维度的长度
    cr_shape = cr.shape
    # 将cr_after 里的--转化为None
    cr_after = cr_after.flatten().tolist()
    cr_after = np.array(cr_after).reshape(cr_shape[0], len(after_lat), len(after_lon))
    # 找到无效值
    invalid_dic = {}
    for i in cr_after[0][0]:
        if i in invalid_dic:
            invalid_dic[i] += 1
        else:
            invalid_dic[i] = 1
    value_max = 0
    key_max = 0
    for key, value in invalid_dic.items():
        if value > value_max:
            if key == None:
                continue
            value_max = value
            key_max = key
    invalid_value = key_max
    if cr_d_count == 3:
        for d in range(20):
            # 取每个时刻的二维数组值
            cr_data = cr_after[d]
            # 消除其中的无效值
            cr_data[cr_data == invalid_value] = np.nan
            cr_data[cr_data == None] = np.nan
            # 获得预报的时间
            forecast_time = timestep[d] + 6
            # xx, yy = np.meshgrid(x, y)
            new_save_dir = '{}{}{}{}{}{}{}'.format(savedir, create_radar, '/', yyyyMMdd, '/', product_type, '/')
            filename = '{}{}{}{}{}{}{}{}'.format(yyyyMMdd, HHmmss, '_', forecast_time, '_', create_method, '_', product_type.lower())
            draw_img(lat_min, lat_max, lon_min, lon_max, after_lon, after_lat, cr_data, new_save_dir, filename)



def draw_img(*args):
    lat_min = args[0]
    lat_max = args[1]
    lon_min = args[2]
    lon_max = args[3]
    xx = args[4]
    yy = args[5]
    data = args[6]
    savedir = args[7]
    filename = args[8]
    xx, yy = np.meshgrid(xx, yy)
    m = Basemap(projection='merc', llcrnrlat=lat_min, urcrnrlat=lat_max, \
                llcrnrlon=lon_min, urcrnrlon=lon_max, lat_ts=5, resolution='c')
    # ax = plt.subplot(1, 1, 1)  # 绘图区
    # 将数据中的无效值的颜色设为透明
    alpha = np.nan
    fig = plt.figure(figsize=(5,5))
    fig.patch.set_alpha(alpha)
    plt.axis('off')
    plt.contourf(xx, yy, data)
    if os.path.exists(savedir) == False:
        os.makedirs(savedir)
    savepath = savedir + '/' + filename
    plt.savefig(savepath, transparent=True, bbox_inches='tight', pad_inches=0)
    plt.close("all")

if __name__ == '__main__':
    # filepath = 'D:\\Data\\20210202_123600.SA.dnn.nc'
    # savedir = r'D:/Data/weatherRadarExtrapolation_parse/'
    filepath = sys.argv[1]
    savedir = sys.argv[2]
    resolve_data(filepath, savedir)