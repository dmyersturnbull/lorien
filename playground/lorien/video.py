import os

import numpy as np
import h5py
import pandas as pd
from scipy import stats
import cv2
#from skimage.transform import downscale_local_mean
#from scipy.misc import imresize
#from skimage.transform import resize


def count_over_tau(frame, prev, threshold):
    diffs = frame - prev > threshold
    return np.count_nonzero(diffs)

def normalize_frame(self, arr):
    mn = (arr).min()
    mx = (arr).max()
    return ((arr - arr.min()) * (255 / (arr.max() - arr.min())))

class Streamer:
    def __init__(self, path: str, roi = None):
        if not os.path.exists(path) or not os.path.isfile(path):
            raise ValueError("File {} not found".format(path))
        self._path = path
        self._cap = cv2.VideoCapture(path)
        self.roi = roi

    def __iter__(self):
        if not self._cap.isOpened(): raise ValueError('Camera object not open!')
        _, raw = self._cap.read()
        prev = cv2.cvtColor(raw, cv2.COLOR_BGR2GRAY)
        while(self._cap.isOpened()):
            _, raw = self._cap.read()
            frame = cv2.cvtColor(raw, cv2.COLOR_BGR2GRAY)
            if self.roi is not None:
                frame = frame[self.roi[0]:self.roi[1], self.roi[2]:self.roi[3]]
            yield frame

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self._cap.release()


class BlockStreamer:
    def __init__(self, iterator, size: int, n_blocks: int, tau = 0):
        self._iterator = iterator
        self.size = size
        self.n_blocks = n_blocks
        self.tau = tau

    def __iter__(self):
        block = []
        prev = None
        for i, frame in enumerate(self._iterator):
            frame = frame.astype(np.float32)
            if i == 0:
                prev = frame
                continue
            diff = frame - prev
            diff[diff<self.tau] = 0
            diff[diff>=self.tau] = 1
            block.append(diff)
            if (i-1) % self.size == self.size - 1:
                yield np.array(block)
                block = []
                if self.n_blocks is not None and (i-1) // self.size > self.n_blocks - 2:
                    break
            prev = frame

    def __enter__(self):
        self._iterator.__enter__()
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self._iterator.__exit__(exc_type, exc_val, exc_tb)
