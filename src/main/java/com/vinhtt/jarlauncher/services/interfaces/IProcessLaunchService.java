package com.vinhtt.jarlauncher.services.interfaces;

import com.vinhtt.jarlauncher.model.JavaProject;

public interface IProcessLaunchService {
    void launchProject(JavaProject project) throws Exception;
}