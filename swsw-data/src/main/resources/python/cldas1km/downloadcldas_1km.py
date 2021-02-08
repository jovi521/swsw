import meteva.base as meb
import numpy as np
import datetime
import xarray as xr
import os
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.pyplot import savefig
from PIL import Image
import sys
import netCDF4 as nc
from mpl_toolkits.basemap import Basemap


def download_cldas_1km(mdfs_path, save_path, ipconfig_path):
    meb.set_io_config(ipconfig_path)
    # 获得文件名
    filename = mdfs_path.split('/')[-1]
    # 获得保存的路径全信息
    # totalpath = os.path.join(save_path, filename)
    totalpath = '{}{}{}'.format(save_path, '/', filename)
    # 如果文件夹不存在，就创建（递归创建）
    if os.path.exists(save_path) == False:
        os.makedirs(save_path)
    # 这里的参数filename为要下载的mdfs文件路径
    grd = meb.read_griddata_from_gds(mdfs_path, show=True)
    if grd is not None:
        meb.write_griddata_to_nc(grd, totalpath)
    else:
        print("{}文件无法读取".format(filename))


if __name__ == '__main__':
    mdfs_path = sys.argv[1]
    save_path = sys.argv[2]
    ipconfig_path = sys.argv[3]
    # mdfs_path = r'mdfs:///CLDAS_1KM/RAIN01/20112809.000'
    # save_path = r'D:/Data/satellite_parse/FY4A/20201127/CHANNEL12'
    # ipconfig_path = 'ip_port.txt'
    download_cldas_1km(mdfs_path, save_path, ipconfig_path)
# download_satellite(r'mdfs:///SATELLITE/FY4A/L1/CHINA/C012/C012_20201126154500_FY4A.AWX', r'D:/data/satellite_parse/FY4A/20201126/CHANNEL02', r'ip_port.txt')
